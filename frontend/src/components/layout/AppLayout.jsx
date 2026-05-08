import React, { useState } from 'react';
import { Outlet } from 'react-router-dom';
import { Sidebar } from './Sidebar';
import { Topbar } from './Topbar';

export function AppLayout() {
  // Manage the state of the sidebar here so both Sidebar and Topbar can access it.
  const [isSidebarCollapsed, setIsSidebarCollapsed] = useState(false);

  return (
    <div className="flex h-screen overflow-hidden bg-background text-foreground">
      {/* Dynamic Sidebar */}
      <Sidebar 
        isCollapsed={isSidebarCollapsed} 
        setIsCollapsed={setIsSidebarCollapsed} 
      />
      
      {/* Main Column */}
      <div className="flex flex-1 flex-col overflow-hidden">
        {/* Top Navigation */}
        <Topbar 
          isSidebarCollapsed={isSidebarCollapsed} 
          setIsSidebarCollapsed={setIsSidebarCollapsed} 
        />
        
        {/* Scrollable Content Area */}
        <main className="flex-1 overflow-y-auto bg-background p-6 md:p-8">
          <div className="mx-auto max-w-7xl w-full h-full">
            <Outlet />
          </div>
        </main>
      </div>
    </div>
  );
}
