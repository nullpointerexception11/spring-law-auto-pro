import React, { lazy, Suspense } from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { Toaster } from 'sonner';
import { Loader2 } from 'lucide-react';

// Layout & Auth
import { AppLayout } from './components/layout/AppLayout';
import { ProtectedRoute } from './components/auth/ProtectedRoute';
import { ErrorBoundary, NotFoundPage } from './components/common/ErrorBoundary';
import { ROUTES } from './lib/constants';

// Lazy Loaded Pages for performance
const LoginPage = lazy(() => import('./pages/LoginPage'));
const DashboardPage = lazy(() => import('./pages/DashboardPage'));
const MatterList = lazy(() => import('./pages/matters/MatterList'));
const MatterDetail = lazy(() => import('./pages/matters/MatterDetail'));
const SuperAdminPage = lazy(() => import('./pages/SuperAdminPage'));
const CalendarPage = lazy(() => import('./pages/CalendarPage'));
const BillingPage = lazy(() => import('./pages/BillingPage'));
const LegalSearchPage = lazy(() => import('./pages/LegalSearchPage'));
const ClientsPage = lazy(() => import('./pages/ClientsPage'));
const SettingsPage = lazy(() => import('./pages/SettingsPage'));

/**
 * Loading component for Suspense
 */
const PageLoader = () => (
  <div className="flex h-screen w-full items-center justify-center bg-background">
    <Loader2 className="h-6 w-6 animate-spin text-primary" />
  </div>
);

function App() {
  return (
    <ErrorBoundary>
      <BrowserRouter>
        <Toaster position="top-right" richColors closeButton />
        
        <Suspense fallback={<PageLoader />}>
          <Routes>
            {/* Auth Routes */}
            <Route path={ROUTES.LOGIN} element={<LoginPage />} />
            
            {/* Protected Application Routes */}
            <Route path="/" element={
              <ProtectedRoute>
                <AppLayout />
              </ProtectedRoute>
            }>
              <Route index element={<Navigate to={ROUTES.DASHBOARD} replace />} />
              <Route path={ROUTES.DASHBOARD} element={<DashboardPage />} />
              <Route path={ROUTES.MATTERS} element={<MatterList />} />
              <Route path={ROUTES.MATTER_DETAIL()} element={<MatterDetail />} />
              <Route path={ROUTES.CALENDAR} element={<CalendarPage />} />
              <Route path={ROUTES.LEGAL_SEARCH} element={<LegalSearchPage />} />
              <Route path={ROUTES.CLIENTS} element={<ClientsPage />} />
              <Route path={ROUTES.BILLING} element={<BillingPage />} />
              <Route path={ROUTES.SETTINGS} element={<SettingsPage />} />
            </Route>

            {/* Restricted Admin Route */}
            <Route path={ROUTES.SUPER_ADMIN} element={
              <ProtectedRoute role="PLATFORM_ADMIN">
                <SuperAdminPage />
              </ProtectedRoute>
            } />

            {/* Real 404 Handling (Item 10) */}
            <Route path="*" element={<NotFoundPage />} />
          </Routes>
        </Suspense>
      </BrowserRouter>
    </ErrorBoundary>
  );
}

export default App;
