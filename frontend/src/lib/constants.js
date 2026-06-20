/**
 * Route constants to avoid hardcoded strings across the app.
 */
export const ROUTES = {
  LOGIN: '/login',
  DASHBOARD: '/dashboard',
  MATTERS: '/matters',
  MATTER_DETAIL: (id) => `/matters/${id || ':matterId'}`,
  CALENDAR: '/calendar',
  DOCUMENTS: '/documents',
  AI: '/ai',
  LEGAL_SEARCH: '/legal-search',
  CLIENTS: '/clients',
  BILLING: '/billing',
  SETTINGS: '/settings',
  SUPER_ADMIN: '/super-admin',
};

/**
 * Common configuration values
 */
export const CONFIG = {
  API_BASE_URL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
  AUTH_TOKEN_KEY: 'token',
  APP_NAME: 'LawAuto Pro',
};
