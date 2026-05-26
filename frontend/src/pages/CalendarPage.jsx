import React, { useState } from 'react';
import { Calendar as CalendarIcon, ChevronLeft, ChevronRight, Clock, MapPin, Gavel, FileText, Plus, Users } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { cn } from '@/lib/utils';

const MONTHS = ['Ocak', 'Şubat', 'Mart', 'Nisan', 'Mayıs', 'Haziran', 'Temmuz', 'Ağustos', 'Eylül', 'Ekim', 'Kasım', 'Aralık'];
const DAYS = ['Pzt', 'Sal', 'Çar', 'Per', 'Cum', 'Cmt', 'Paz'];

const MOCK_EVENTS = [
  { id: 1, title: 'İş Mahkemesi Duruşması', matter: 'İşçi Alacakları Davası', date: '2026-05-28', time: '10:00', location: 'İstanbul Adliyesi 3. Kat', type: 'hearing' },
  { id: 2, title: 'Bilirkişi İncelemesi', matter: 'Tazminat Davası', date: '2026-05-29', time: '14:30', location: 'Bakırköy Adliyesi', type: 'expert' },
  { id: 3, title: 'Dilekçe Son Teslim', matter: 'Sözleşme İhtilafı', date: '2026-05-30', time: '17:00', location: '', type: 'deadline' },
  { id: 4, title: 'Arabuluculuk Toplantısı', matter: 'İşçi Alacakları Davası', date: '2026-06-02', time: '11:00', location: 'Levent Plaza', type: 'mediation' },
  { id: 5, title: 'İstinaf Duruşması', matter: 'Tazminat Davası', date: '2026-06-05', time: '09:30', location: 'İstanbul Bölge Adliye Mahkemesi', type: 'hearing' },
  { id: 6, title: 'Tanık Dinletme', matter: 'Sözleşme İhtilafı', date: '2026-06-10', time: '14:00', location: 'İstanbul Adliyesi', type: 'hearing' },
];

const UPCOMING_EVENTS = MOCK_EVENTS.filter(e => new Date(e.date) >= new Date()).sort((a, b) => new Date(a.date) - new Date(b.date)).slice(0, 5);

const EVENT_CONFIG = {
  hearing: { label: 'Duruşma', icon: Gavel, className: 'bg-blue-50 border-blue-200 text-blue-700 dark:bg-blue-950 dark:border-blue-800 dark:text-blue-300' },
  expert: { label: 'Bilirkişi', icon: FileText, className: 'bg-amber-50 border-amber-200 text-amber-700 dark:bg-amber-950 dark:border-amber-800 dark:text-amber-300' },
  deadline: { label: 'Son Tarih', icon: Clock, className: 'bg-red-50 border-red-200 text-red-700 dark:bg-red-950 dark:border-red-800 dark:text-red-300' },
  mediation: { label: 'Arabuluculuk', icon: Users, className: 'bg-emerald-50 border-emerald-200 text-emerald-700 dark:bg-emerald-950 dark:border-emerald-800 dark:text-emerald-300' },
};

export default function CalendarPage() {
  const today = new Date();
  const [currentMonth, setCurrentMonth] = useState(today.getMonth());
  const [currentYear, setCurrentYear] = useState(today.getFullYear());
  const [selectedDate, setSelectedDate] = useState(null);

  const firstDay = new Date(currentYear, currentMonth, 1).getDay();
  const daysInMonth = new Date(currentYear, currentMonth + 1, 0).getDate();
  const adjustedFirstDay = firstDay === 0 ? 6 : firstDay - 1;

  const prevMonth = () => {
    if (currentMonth === 0) { setCurrentMonth(11); setCurrentYear(currentYear - 1); }
    else setCurrentMonth(currentMonth - 1);
    setSelectedDate(null);
  };

  const nextMonth = () => {
    if (currentMonth === 11) { setCurrentMonth(0); setCurrentYear(currentYear + 1); }
    else setCurrentMonth(currentMonth + 1);
    setSelectedDate(null);
  };

  const todayStr = today.toISOString().split('T')[0];
  
  const getEventsForDate = (year, month, day) => {
    const dateStr = `${year}-${String(month + 1).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
    return MOCK_EVENTS.filter(e => e.date === dateStr);
  };

  const selectedEvents = selectedDate ? getEventsForDate(currentYear, currentMonth, selectedDate) : [];

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-xl font-semibold text-foreground">Takvim</h1>
          <p className="text-sm text-muted-foreground mt-0.5">Duruşma, toplantı ve son tarihlerinizi takip edin.</p>
        </div>
        <Button size="sm">
          <Plus className="h-4 w-4 mr-1.5" /> Yeni Etkinlik
        </Button>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Calendar */}
        <Card className="lg:col-span-2">
          <CardHeader className="flex flex-row items-center justify-between pb-2">
            <CardTitle className="text-sm font-medium">
              {MONTHS[currentMonth]} {currentYear}
            </CardTitle>
            <div className="flex items-center gap-1">
              <Button variant="ghost" size="icon" className="h-7 w-7" onClick={prevMonth}>
                <ChevronLeft className="h-4 w-4" />
              </Button>
              <Button variant="ghost" size="sm" className="h-7 text-xs" onClick={() => { setCurrentMonth(today.getMonth()); setCurrentYear(today.getFullYear()); }}>
                Bugün
              </Button>
              <Button variant="ghost" size="icon" className="h-7 w-7" onClick={nextMonth}>
                <ChevronRight className="h-4 w-4" />
              </Button>
            </div>
          </CardHeader>
          <CardContent>
            <div className="grid grid-cols-7 mb-2">
              {DAYS.map(day => (
                <div key={day} className="text-center text-[11px] font-medium text-muted-foreground py-1.5">
                  {day}
                </div>
              ))}
            </div>
            <div className="grid grid-cols-7 gap-px bg-border rounded-lg overflow-hidden">
              {[...Array(adjustedFirstDay)].map((_, i) => (
                <div key={`empty-${i}`} className="bg-card p-2 min-h-[60px]" />
              ))}
              {[...Array(daysInMonth)].map((_, i) => {
                const day = i + 1;
                const dateStr = `${currentYear}-${String(currentMonth + 1).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
                const isToday = dateStr === todayStr;
                const isSelected = selectedDate === day;
                const events = getEventsForDate(currentYear, currentMonth, day);
                
                return (
                  <button
                    key={day}
                    onClick={() => setSelectedDate(isSelected ? null : day)}
                    className={cn(
                      'bg-card p-1.5 min-h-[60px] text-left hover:bg-accent transition-colors relative',
                      isSelected && 'ring-1 ring-inset ring-primary bg-primary/5'
                    )}
                  >
                    <span className={cn(
                      'inline-flex items-center justify-center w-6 h-6 rounded-md text-xs tabular-nums',
                      isToday && 'bg-primary text-primary-foreground font-semibold',
                      !isToday && 'text-foreground'
                    )}>
                      {day}
                    </span>
                    <div className="mt-0.5 space-y-0.5">
                      {events.slice(0, 2).map(event => {
                        const config = EVENT_CONFIG[event.type];
                        const Icon = config.icon;
                        return (
                          <div key={event.id} className="flex items-center gap-1 text-[9px] text-muted-foreground truncate">
                            <Icon className="h-2.5 w-2.5 shrink-0" />
                            <span className="truncate">{event.title}</span>
                          </div>
                        );
                      })}
                      {events.length > 2 && (
                        <span className="text-[9px] text-muted-foreground">+{events.length - 2} daha</span>
                      )}
                    </div>
                  </button>
                );
              })}
            </div>
          </CardContent>
        </Card>

        {/* Events Panel */}
        <div className="space-y-4">
          <Card>
            <CardHeader className="pb-3">
              <CardTitle className="text-sm font-medium">
                {selectedDate 
                  ? `${selectedDate} ${MONTHS[currentMonth]} ${currentYear}`
                  : 'Yaklaşan Etkinlikler'}
              </CardTitle>
            </CardHeader>
            <CardContent>
              {selectedDate && selectedEvents.length > 0 ? (
                <div className="space-y-3">
                  {selectedEvents.map(event => {
                    const config = EVENT_CONFIG[event.type];
                    const Icon = config.icon;
                    return (
                      <div key={event.id} className={cn('p-3 rounded-lg border', config.className)}>
                        <div className="flex items-center gap-2 mb-1.5">
                          <Icon className="h-3.5 w-3.5" />
                          <Badge variant="outline" className="text-[9px] font-medium border-transparent bg-transparent p-0 h-auto">
                            {config.label}
                          </Badge>
                        </div>
                        <p className="text-sm font-medium mb-1">{event.title}</p>
                        <p className="text-[11px] opacity-80">{event.matter}</p>
                        <div className="flex items-center gap-2 mt-2 text-[11px] opacity-70">
                          <Clock className="h-3 w-3" />
                          {event.time}
                          {event.location && (
                            <>
                              <span className="mx-0.5">-</span>
                              <MapPin className="h-3 w-3" />
                              <span className="truncate">{event.location}</span>
                            </>
                          )}
                        </div>
                      </div>
                    );
                  })}
                </div>
              ) : selectedDate && selectedEvents.length === 0 ? (
                <div className="text-center py-8">
                  <CalendarIcon className="h-8 w-8 text-muted-foreground mx-auto mb-2" />
                  <p className="text-sm text-muted-foreground">Bu tarihte etkinlik yok</p>
                </div>
              ) : (
                <div className="space-y-2">
                  {UPCOMING_EVENTS.length > 0 ? (
                    UPCOMING_EVENTS.map(event => {
                      const config = EVENT_CONFIG[event.type];
                      const Icon = config.icon;
                      const eventDate = new Date(event.date);
                      return (
                        <div key={event.id} className="flex items-start gap-3 p-3 rounded-lg hover:bg-accent transition-colors">
                          <div className={cn(
                            'h-7 w-7 rounded-md flex items-center justify-center shrink-0',
                            event.type === 'hearing' && 'bg-blue-100 text-blue-600 dark:bg-blue-900 dark:text-blue-400',
                            event.type === 'expert' && 'bg-amber-100 text-amber-600 dark:bg-amber-900 dark:text-amber-400',
                            event.type === 'deadline' && 'bg-red-100 text-red-600 dark:bg-red-900 dark:text-red-400',
                            event.type === 'mediation' && 'bg-emerald-100 text-emerald-600 dark:bg-emerald-900 dark:text-emerald-400',
                          )}>
                            <Icon className="h-3.5 w-3.5" />
                          </div>
                          <div className="flex-1 min-w-0">
                            <p className="text-sm font-medium text-foreground truncate">{event.title}</p>
                            <p className="text-[11px] text-muted-foreground">{event.matter}</p>
                            <div className="flex items-center gap-2 mt-1 text-[10px] text-muted-foreground">
                              <span className="font-medium">
                                {eventDate.toLocaleDateString('tr-TR', { day: 'numeric', month: 'short' })}
                              </span>
                              <span>{event.time}</span>
                            </div>
                          </div>
                        </div>
                      );
                    })
                  ) : (
                    <div className="text-center py-8">
                      <CalendarIcon className="h-8 w-8 text-muted-foreground mx-auto mb-2" />
                      <p className="text-sm text-muted-foreground">Yaklaşan etkinlik yok</p>
                    </div>
                  )}
                </div>
              )}
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  );
}
