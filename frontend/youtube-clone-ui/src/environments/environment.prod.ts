import * as env from "../../auth_config.json";

const {domain, clientId, scope, responseType, silentRenew, useRefreshToken, secureRoutes, customParamsAuthRequest} = env

export const environment = {
  production: true,
  auth: {
    domain,
    clientId,
    scope,
    responseType,
    silentRenew,
    useRefreshToken,
    secureRoutes,
    customParamsAuthRequest,
    redirectUrl: window.location.origin
  }
};
