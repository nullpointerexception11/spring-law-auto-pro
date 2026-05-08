import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AppLayout } from './components/layout/AppLayout';
import MatterList from './pages/matters/MatterList';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<AppLayout />}>
          {/* Automatically redirect to the primary working space */}
          <Route index element={<Navigate to="/matters" replace />} />
          
          {/* Vertical Slices */}
          <Route path="matters" element={<MatterList />} />
          <Route path="matters/:matterId" element={<div className="fade-enter-active">Matter Detail View (Coming Soon)</div>} />
          
          {/* Navigation Stubs */}
          <Route path="calendar" element={<div className="fade-enter-active">Calendar</div>} />
          <Route path="documents" element={<div className="fade-enter-active">Documents</div>} />
          <Route path="billing" element={<div className="fade-enter-active">Billing</div>} />
          <Route path="ai" element={<div className="fade-enter-active">AI Research</div>} />
          <Route path="notifications" element={<div className="fade-enter-active">Notifications</div>} />
          <Route path="settings" element={<div className="fade-enter-active">Settings</div>} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}

export default App;
