import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AppLayout } from './components/layout/AppLayout';
import LoginPage from './pages/LoginPage';
import MatterList from './pages/matters/MatterList';
import MatterDetail from './pages/matters/MatterDetail';

const ProtectedRoute = ({ children }) => {
  const token = localStorage.getItem('token');
  if (!token) {
    return <Navigate to="/login" replace />;
  }
  return children;
};

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        
        <Route path="/" element={
          <ProtectedRoute>
            <AppLayout />
          </ProtectedRoute>
        }>
          {/* Automatically redirect to the primary working space */}
          <Route index element={<Navigate to="/matters" replace />} />
          
          {/* Vertical Slices */}
          <Route path="matters" element={<MatterList />} />
          <Route path="matters/:matterId" element={<MatterDetail />} />
          
          {/* Navigation Stubs */}
          <Route path="calendar" element={<div className="fade-enter-active">Calendar</div>} />
          <Route path="documents" element={<div className="fade-enter-active">Documents</div>} />
          <Route path="billing" element={<div className="fade-enter-active">Billing</div>} />
          <Route path="ai" element={<div className="fade-enter-active">AI Research</div>} />
          <Route path="notifications" element={<div className="fade-enter-active">Notifications</div>} />
          <Route path="settings" element={<div className="fade-enter-active">Settings</div>} />
        </Route>

        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
