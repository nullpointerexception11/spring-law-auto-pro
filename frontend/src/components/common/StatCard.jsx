import React from 'react';
import { Card, CardContent } from "@/components/ui/card";

export function StatCard({ icon, label, value }) {
  return (
    <Card className="shadow-sm border-border bg-card">
      <CardContent className="p-5 flex flex-col justify-between">
        <div className="flex items-center justify-between mb-4">
          <p className="text-sm font-medium text-muted-foreground">{label}</p>
          <div className="text-muted-foreground">
            {icon}
          </div>
        </div>
        <h4 className="text-2xl font-bold text-foreground">{value}</h4>
      </CardContent>
    </Card>
  );
}
