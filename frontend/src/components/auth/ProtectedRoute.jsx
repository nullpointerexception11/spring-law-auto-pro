import React from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useAuthStore } from '@/store/useAuthStore';

/**
 * Route guard component that checks for authentication and optional role requirements.
 * 
 * @param {React.ReactNode} children - The component to render if access is granted.
 * @param {string} role - (Optional) Required role to access the route.
 * @param {string} permission - (Optional) Specific permission required.
 */
export const ProtectedRoute = ({ children, role, permission }) => {
  const { isAuthenticated, role: userRole, hasPermission } = useAuthStore();
  const location = useLocation();

  // 1. Check basic authentication
  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  // 2. Check role requirement if provided
  if (role && userRole !== role) {
    // If user is logged in but doesn't have the role, redirect to dashboard or error page
    return <Navigate to="/" replace />;
  }

  // 3. Check specific permission if provided
  if (permission && !hasPermission(permission)) {
    return <Navigate to="/" replace />;
  }

  return children;
};
