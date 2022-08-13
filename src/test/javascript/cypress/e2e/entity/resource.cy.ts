import {
  entityTableSelector,
  entityDetailsButtonSelector,
  entityDetailsBackButtonSelector,
  entityCreateButtonSelector,
  entityCreateSaveButtonSelector,
  entityCreateCancelButtonSelector,
  entityEditButtonSelector,
  entityDeleteButtonSelector,
  entityConfirmDeleteButtonSelector,
} from '../../support/entity';

describe('Resource e2e test', () => {
  const resourcePageUrl = '/resource';
  const resourcePageUrlPattern = new RegExp('/resource(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const resourceSample = { title: 'withdrawal' };

  let resource;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/resources+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/resources').as('postEntityRequest');
    cy.intercept('DELETE', '/api/resources/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (resource) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/resources/${resource.id}`,
      }).then(() => {
        resource = undefined;
      });
    }
  });

  it('Resources menu should load Resources page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('resource');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Resource').should('exist');
    cy.url().should('match', resourcePageUrlPattern);
  });

  describe('Resource page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(resourcePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Resource page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/resource/new$'));
        cy.getEntityCreateUpdateHeading('Resource');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', resourcePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/resources',
          body: resourceSample,
        }).then(({ body }) => {
          resource = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/resources+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/resources?page=0&size=20>; rel="last",<http://localhost/api/resources?page=0&size=20>; rel="first"',
              },
              body: [resource],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(resourcePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Resource page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('resource');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', resourcePageUrlPattern);
      });

      it('edit button click should load edit Resource page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Resource');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', resourcePageUrlPattern);
      });

      it('edit button click should load edit Resource page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Resource');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', resourcePageUrlPattern);
      });

      it('last delete button click should delete instance of Resource', () => {
        cy.intercept('GET', '/api/resources/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('resource').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', resourcePageUrlPattern);

        resource = undefined;
      });
    });
  });

  describe('new Resource page', () => {
    beforeEach(() => {
      cy.visit(`${resourcePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Resource');
    });

    it('should create an instance of Resource', () => {
      cy.get(`[data-cy="title"]`).type('Tactics set').should('have.value', 'Tactics set');

      cy.get(`[data-cy="creationDate"]`).type('2022-08-13').blur().should('have.value', '2022-08-13');

      cy.get(`[data-cy="description"]`).type('Streamlined').should('have.value', 'Streamlined');

      cy.get(`[data-cy="resourceType"]`).select('PRESENTATION');

      cy.get(`[data-cy="angeRage"]`).select('AGE_16_18');

      cy.setFieldImageAsBytesOfEntity('file', 'integration-test.png', 'image/png');

      cy.get(`[data-cy="url"]`).type('https://rachelle.biz').should('have.value', 'https://rachelle.biz');

      cy.get(`[data-cy="author"]`).type('backing').should('have.value', 'backing');

      cy.get(`[data-cy="lastUpdated"]`).type('2022-08-13').blur().should('have.value', '2022-08-13');

      cy.get(`[data-cy="activated"]`).should('not.be.checked');
      cy.get(`[data-cy="activated"]`).click().should('be.checked');

      cy.get(`[data-cy="views"]`).type('62951').should('have.value', '62951');

      cy.get(`[data-cy="votes"]`).type('49668').should('have.value', '49668');

      cy.get(`[data-cy="approvedBy"]`).type('Garden streamline').should('have.value', 'Garden streamline');

      // since cypress clicks submit too fast before the blob fields are validated
      cy.wait(200); // eslint-disable-line cypress/no-unnecessary-waiting
      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        resource = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', resourcePageUrlPattern);
    });
  });
});
