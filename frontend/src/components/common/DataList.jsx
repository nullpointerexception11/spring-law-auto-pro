import React from 'react';
import { cn } from "@/lib/utils";

export function DataList({ items, renderItem, className }) {
  return (
    <div className={cn("divide-y divide-border", className)}>
      {items.map((item, index) => (
        <div key={index} className="p-4 hover:bg-muted/50 transition-colors cursor-pointer group">
          {renderItem(item, index)}
        </div>
      ))}
    </div>
  );
}

export function DataListItem({ icon, title, description, rightContent, className }) {
  return (
    <div className={cn("flex items-center justify-between", className)}>
      <div className="flex items-center gap-3">
        {icon && (
          <div className="w-10 h-10 border border-border rounded-md flex items-center justify-center bg-background text-muted-foreground group-hover:border-primary/20 transition-colors">
            {icon}
          </div>
        )}
        <div>
          <p className="text-sm font-medium text-foreground">{title}</p>
          {description && <p className="text-xs text-muted-foreground mt-0.5">{description}</p>}
        </div>
      </div>
      {rightContent && (
        <div className="text-right flex items-center gap-4">
          {rightContent}
        </div>
      )}
    </div>
  );
}
