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

describe('Skill e2e test', () => {
  const skillPageUrl = '/skill';
  const skillPageUrlPattern = new RegExp('/skill(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const skillSample = {};

  let skill;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/skills+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/skills').as('postEntityRequest');
    cy.intercept('DELETE', '/api/skills/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (skill) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/skills/${skill.id}`,
      }).then(() => {
        skill = undefined;
      });
    }
  });

  it('Skills menu should load Skills page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('skill');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Skill').should('exist');
    cy.url().should('match', skillPageUrlPattern);
  });

  describe('Skill page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(skillPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Skill page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/skill/new$'));
        cy.getEntityCreateUpdateHeading('Skill');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', skillPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/skills',
          body: skillSample,
        }).then(({ body }) => {
          skill = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/skills+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/skills?page=0&size=20>; rel="last",<http://localhost/api/skills?page=0&size=20>; rel="first"',
              },
              body: [skill],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(skillPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Skill page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('skill');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', skillPageUrlPattern);
      });

      it('edit button click should load edit Skill page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Skill');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', skillPageUrlPattern);
      });

      it('edit button click should load edit Skill page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Skill');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', skillPageUrlPattern);
      });

      it('last delete button click should delete instance of Skill', () => {
        cy.intercept('GET', '/api/skills/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('skill').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', skillPageUrlPattern);

        skill = undefined;
      });
    });
  });

  describe('new Skill page', () => {
    beforeEach(() => {
      cy.visit(`${skillPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Skill');
    });

    it('should create an instance of Skill', () => {
      cy.get(`[data-cy="label"]`).type('calculate Kip').should('have.value', 'calculate Kip');

      cy.get(`[data-cy="creationDate"]`).type('2022-08-12').blur().should('have.value', '2022-08-12');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        skill = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', skillPageUrlPattern);
    });
  });
});
