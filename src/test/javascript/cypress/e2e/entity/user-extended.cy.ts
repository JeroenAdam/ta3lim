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

describe('UserExtended e2e test', () => {
  const userExtendedPageUrl = '/user-extended';
  const userExtendedPageUrlPattern = new RegExp('/user-extended(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const userExtendedSample = {};

  let userExtended;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/user-extendeds+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/user-extendeds').as('postEntityRequest');
    cy.intercept('DELETE', '/api/user-extendeds/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (userExtended) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/user-extendeds/${userExtended.id}`,
      }).then(() => {
        userExtended = undefined;
      });
    }
  });

  it('UserExtendeds menu should load UserExtendeds page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('user-extended');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('UserExtended').should('exist');
    cy.url().should('match', userExtendedPageUrlPattern);
  });

  describe('UserExtended page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(userExtendedPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create UserExtended page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/user-extended/new$'));
        cy.getEntityCreateUpdateHeading('UserExtended');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', userExtendedPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/user-extendeds',
          body: userExtendedSample,
        }).then(({ body }) => {
          userExtended = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/user-extendeds+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/user-extendeds?page=0&size=20>; rel="last",<http://localhost/api/user-extendeds?page=0&size=20>; rel="first"',
              },
              body: [userExtended],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(userExtendedPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details UserExtended page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('userExtended');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', userExtendedPageUrlPattern);
      });

      it('edit button click should load edit UserExtended page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('UserExtended');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', userExtendedPageUrlPattern);
      });

      it('edit button click should load edit UserExtended page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('UserExtended');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', userExtendedPageUrlPattern);
      });

      it('last delete button click should delete instance of UserExtended', () => {
        cy.intercept('GET', '/api/user-extendeds/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('userExtended').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', userExtendedPageUrlPattern);

        userExtended = undefined;
      });
    });
  });

  describe('new UserExtended page', () => {
    beforeEach(() => {
      cy.visit(`${userExtendedPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('UserExtended');
    });

    it('should create an instance of UserExtended', () => {
      cy.get(`[data-cy="lastLogin"]`).type('2022-08-13').blur().should('have.value', '2022-08-13');

      cy.get(`[data-cy="aboutMe"]`).type('Fields').should('have.value', 'Fields');

      cy.get(`[data-cy="occupation"]`).type('Rubber SMS Rubber').should('have.value', 'Rubber SMS Rubber');

      cy.get(`[data-cy="socialMedia"]`).type('Chief').should('have.value', 'Chief');

      cy.get(`[data-cy="civilStatus"]`).select('OTHER');

      cy.get(`[data-cy="firstchild"]`).select('AGE_16_18');

      cy.get(`[data-cy="secondchild"]`).select('AGE_13_15');

      cy.get(`[data-cy="thirdchild"]`).select('AGE_13_15');

      cy.get(`[data-cy="fourthchild"]`).select('AGE_07_09');

      cy.get(`[data-cy="filesquota"]`).type('35812').should('have.value', '35812');

      cy.get(`[data-cy="approverSince"]`).type('2022-08-13').blur().should('have.value', '2022-08-13');

      cy.get(`[data-cy="lastApproval"]`).type('2022-08-13').blur().should('have.value', '2022-08-13');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        userExtended = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', userExtendedPageUrlPattern);
    });
  });
});
