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

describe('Votes e2e test', () => {
  const votesPageUrl = '/votes';
  const votesPageUrlPattern = new RegExp('/votes(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const votesSample = {};

  let votes;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/votes+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/votes').as('postEntityRequest');
    cy.intercept('DELETE', '/api/votes/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (votes) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/votes/${votes.id}`,
      }).then(() => {
        votes = undefined;
      });
    }
  });

  it('Votes menu should load Votes page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('votes');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Votes').should('exist');
    cy.url().should('match', votesPageUrlPattern);
  });

  describe('Votes page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(votesPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Votes page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/votes/new$'));
        cy.getEntityCreateUpdateHeading('Votes');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', votesPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/votes',
          body: votesSample,
        }).then(({ body }) => {
          votes = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/votes+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/votes?page=0&size=20>; rel="last",<http://localhost/api/votes?page=0&size=20>; rel="first"',
              },
              body: [votes],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(votesPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Votes page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('votes');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', votesPageUrlPattern);
      });

      it('edit button click should load edit Votes page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Votes');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', votesPageUrlPattern);
      });

      it('edit button click should load edit Votes page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Votes');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', votesPageUrlPattern);
      });

      it('last delete button click should delete instance of Votes', () => {
        cy.intercept('GET', '/api/votes/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('votes').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', votesPageUrlPattern);

        votes = undefined;
      });
    });
  });

  describe('new Votes page', () => {
    beforeEach(() => {
      cy.visit(`${votesPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Votes');
    });

    it('should create an instance of Votes', () => {
      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        votes = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', votesPageUrlPattern);
    });
  });
});
