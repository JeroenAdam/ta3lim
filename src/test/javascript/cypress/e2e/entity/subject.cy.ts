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

describe('Subject e2e test', () => {
  const subjectPageUrl = '/subject';
  const subjectPageUrlPattern = new RegExp('/subject(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const subjectSample = {};

  let subject;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/subjects+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/subjects').as('postEntityRequest');
    cy.intercept('DELETE', '/api/subjects/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (subject) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/subjects/${subject.id}`,
      }).then(() => {
        subject = undefined;
      });
    }
  });

  it('Subjects menu should load Subjects page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('subject');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Subject').should('exist');
    cy.url().should('match', subjectPageUrlPattern);
  });

  describe('Subject page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(subjectPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Subject page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/subject/new$'));
        cy.getEntityCreateUpdateHeading('Subject');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', subjectPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/subjects',
          body: subjectSample,
        }).then(({ body }) => {
          subject = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/subjects+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/subjects?page=0&size=20>; rel="last",<http://localhost/api/subjects?page=0&size=20>; rel="first"',
              },
              body: [subject],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(subjectPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Subject page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('subject');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', subjectPageUrlPattern);
      });

      it('edit button click should load edit Subject page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Subject');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', subjectPageUrlPattern);
      });

      it('edit button click should load edit Subject page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Subject');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', subjectPageUrlPattern);
      });

      it('last delete button click should delete instance of Subject', () => {
        cy.intercept('GET', '/api/subjects/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('subject').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', subjectPageUrlPattern);

        subject = undefined;
      });
    });
  });

  describe('new Subject page', () => {
    beforeEach(() => {
      cy.visit(`${subjectPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Subject');
    });

    it('should create an instance of Subject', () => {
      cy.get(`[data-cy="label"]`).type('Plastic Loan').should('have.value', 'Plastic Loan');

      cy.get(`[data-cy="creationDate"]`).type('2022-08-12').blur().should('have.value', '2022-08-12');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        subject = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', subjectPageUrlPattern);
    });
  });
});
