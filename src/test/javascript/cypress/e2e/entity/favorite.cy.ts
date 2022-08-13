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

describe('Favorite e2e test', () => {
  const favoritePageUrl = '/favorite';
  const favoritePageUrlPattern = new RegExp('/favorite(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const favoriteSample = {};

  let favorite;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/favorites+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/favorites').as('postEntityRequest');
    cy.intercept('DELETE', '/api/favorites/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (favorite) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/favorites/${favorite.id}`,
      }).then(() => {
        favorite = undefined;
      });
    }
  });

  it('Favorites menu should load Favorites page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('favorite');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Favorite').should('exist');
    cy.url().should('match', favoritePageUrlPattern);
  });

  describe('Favorite page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(favoritePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Favorite page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/favorite/new$'));
        cy.getEntityCreateUpdateHeading('Favorite');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', favoritePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/favorites',
          body: favoriteSample,
        }).then(({ body }) => {
          favorite = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/favorites+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/favorites?page=0&size=20>; rel="last",<http://localhost/api/favorites?page=0&size=20>; rel="first"',
              },
              body: [favorite],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(favoritePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Favorite page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('favorite');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', favoritePageUrlPattern);
      });

      it('edit button click should load edit Favorite page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Favorite');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', favoritePageUrlPattern);
      });

      it('edit button click should load edit Favorite page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Favorite');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', favoritePageUrlPattern);
      });

      it('last delete button click should delete instance of Favorite', () => {
        cy.intercept('GET', '/api/favorites/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('favorite').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', favoritePageUrlPattern);

        favorite = undefined;
      });
    });
  });

  describe('new Favorite page', () => {
    beforeEach(() => {
      cy.visit(`${favoritePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Favorite');
    });

    it('should create an instance of Favorite', () => {
      cy.get(`[data-cy="creationDate"]`).type('2022-08-12').blur().should('have.value', '2022-08-12');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        favorite = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', favoritePageUrlPattern);
    });
  });
});
