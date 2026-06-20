import React, { memo, useCallback, useMemo, useState } from 'react';
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

const EVENT_CONFIG = {
  hearing: { label: 'Duruşma', icon: Gavel, className: 'bg-blue-50 border-blue-200 text-blue-700 dark:bg-blue-950 dark:border-blue-800 dark:text-blue-300' },
  expert: { label: 'Bilirkişi', icon: FileText, className: 'bg-amber-50 border-amber-200 text-amber-700 dark:bg-amber-950 dark:border-amber-800 dark:text-amber-300' },
  deadline: { label: 'Son Tarih', icon: Clock, className: 'bg-red-50 border-red-200 text-red-700 dark:bg-red-950 dark:border-red-800 dark:text-red-300' },
  mediation: { label: 'Arabuluculuk', icon: Users, className: 'bg-emerald-50 border-emerald-200 text-emerald-700 dark:bg-emerald-950 dark:border-emerald-800 dark:text-emerald-300' },
};

const today = new Date();
const todayStr = today.toISOString().split('T')[0];

const dateKey = (year, month, day) => `${year}-${String(month + 1).padStart(2, '0')}-${String(day).padStart(2, '0')}`;

const getEventsForDate = (year, month, day) => {
  const key = dateKey(year, month, day);
  return MOCK_EVENTS.filter((event) => event.date === key);
};

const UPCOMING_EVENTS = MOCK_EVENTS.filter((event) => new Date(event.date) >= today)
  .sort((a, b) => new Date(a.date) - new Date(b.date))
  .slice(0, 5);

const EventPreview = memo(function EventPreview({ event }) {
  const config = EVENT_CONFIG[event.type];
  const Icon = config.icon;

  return (
    <div className="flex items-center gap-1 text-[9px] text-muted-foreground truncate">
      <Icon className="h-2.5 w-2.5 shrink-0" />
      <span className="truncate">{event.title}</span>
    </div>
  );
});

const UpcomingEvent = memo(function UpcomingEvent({ event }) {
  const config = EVENT_CONFIG[event.type];
  const Icon = config.icon;
  const eventDate = new Date(event.date);

  return (
    <div className="flex items-start gap-3 p-3 rounded-lg hover:bg-accent transition-colors">
      <div
        className={cn(
          'h-7 w-7 rounded-md flex items-center justify-center shrink-0',
          event.type === 'hearing' && 'bg-blue-100 text-blue-600 dark:bg-blue-900 dark:text-blue-400',
          event.type === 'expert' && 'bg-amber-100 text-amber-600 dark:bg-amber-900 dark:text-amber-400',
          event.type === 'deadline' && 'bg-red-100 text-red-600 dark:bg-red-900 dark:text-red-400',
          event.type === 'mediation' && 'bg-emerald-100 text-emerald-600 dark:bg-emerald-900 dark:text-emerald-400'
        )}
      >
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
});

function CalendarPageComponent() {
  const [currentMonth, setCurrentMonth] = useState(today.getMonth());
  const [currentYear, setCurrentYear] = useState(today.getFullYear());
  const [selectedDate, setSelectedDate] = useState(null);

  const { adjustedFirstDay, daysInMonth } = useMemo(() => {
    const firstDay = new Date(currentYear, currentMonth, 1).getDay();
    return {
      adjustedFirstDay: firstDay === 0 ? 6 : firstDay - 1,
      daysInMonth: new Date(currentYear, currentMonth + 1, 0).getDate(),
    };
  }, [currentMonth, currentYear]);

  const selectedEvents = useMemo(() => {
    if (!selectedDate) return [];
    return getEventsForDate(currentYear, currentMonth, selectedDate);
  }, [currentMonth, currentYear, selectedDate]);

  const goToToday = useCallback(() => {
    setCurrentMonth(today.getMonth());
    setCurrentYear(today.getFullYear());
    setSelectedDate(null);
  }, []);

  const prevMonth = useCallback(() => {
    setCurrentMonth((month) => {
      if (month === 0) {
        setCurrentYear((year) => year - 1);
        return 11;
      }
      return month - 1;
    });
    setSelectedDate(null);
  }, []);

  const nextMonth = useCallback(() => {
    setCurrentMonth((month) => {
      if (month === 11) {
        setCurrentYear((year) => year + 1);
        return 0;
      }
      return month + 1;
    });
    setSelectedDate(null);
  }, []);

  const toggleSelectedDate = useCallback((day) => {
    setSelectedDate((current) => (current === day ? null : day));
  }, []);

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <div className="flex items-center gap-2">
            <h1 className="text-xl font-semibold text-foreground">Takvim</h1>
            <Badge variant="outline" className="text-[10px] border-amber-200 bg-amber-50 text-amber-700 dark:border-amber-900 dark:bg-amber-950/40 dark:text-amber-300">
              Önizleme
            </Badge>
          </div>
          <p className="text-sm text-muted-foreground mt-0.5">Duruşma, toplantı ve son tarihlerinizi takip edin.</p>
        </div>
        <Button size="sm" variant="outline" disabled title="Bu ekran şu anda backend'e bağlı değil">
          <Plus className="h-4 w-4 mr-1.5" /> Yeni Etkinlik
        </Button>
      </div>

      <Card className="border-amber-200 bg-amber-50/60 dark:border-amber-900/60 dark:bg-amber-950/20">
        <CardContent className="flex items-start gap-3 p-4">
          <Badge variant="outline" className="shrink-0 text-[10px] border-amber-200 bg-background text-amber-700 dark:border-amber-900 dark:text-amber-300">
            Önizleme
          </Badge>
          <p className="text-sm text-amber-900/90 dark:text-amber-100/90">
            Bu takvim henüz gerçek backend etkinliklerine bağlı değil. Şimdilik yalnızca arayüz ve davranış denemesi için tutuluyor.
          </p>
        </CardContent>
      </Card>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <Card className="lg:col-span-2">
          <CardHeader className="flex flex-row items-center justify-between pb-2">
            <CardTitle className="text-sm font-medium">
              {MONTHS[currentMonth]} {currentYear}
            </CardTitle>
            <div className="flex items-center gap-1">
              <Button variant="ghost" size="icon" className="h-7 w-7" onClick={prevMonth}>
                <ChevronLeft className="h-4 w-4" />
              </Button>
              <Button variant="ghost" size="sm" className="h-7 text-xs" onClick={goToToday}>
                Bugün
              </Button>
              <Button variant="ghost" size="icon" className="h-7 w-7" onClick={nextMonth}>
                <ChevronRight className="h-4 w-4" />
              </Button>
            </div>
          </CardHeader>
          <CardContent>
            <div className="grid grid-cols-7 mb-2">
              {DAYS.map((day) => (
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
                const key = dateKey(currentYear, currentMonth, day);
                const isToday = key === todayStr;
                const isSelected = selectedDate === day;
                const events = getEventsForDate(currentYear, currentMonth, day);

                return (
                  <button
                    key={day}
                    type="button"
                    onClick={() => toggleSelectedDate(day)}
                    className={cn(
                      'bg-card p-1.5 min-h-[60px] text-left hover:bg-accent transition-colors relative',
                      isSelected && 'ring-1 ring-inset ring-primary bg-primary/5'
                    )}
                  >
                    <span
                      className={cn(
                        'inline-flex items-center justify-center w-6 h-6 rounded-md text-xs tabular-nums',
                        isToday && 'bg-primary text-primary-foreground font-semibold',
                        !isToday && 'text-foreground'
                      )}
                    >
                      {day}
                    </span>
                    <div className="mt-0.5 space-y-0.5">
                      {events.slice(0, 2).map((event) => (
                        <EventPreview key={event.id} event={event} />
                      ))}
                      {events.length > 2 && <span className="text-[9px] text-muted-foreground">+{events.length - 2} daha</span>}
                    </div>
                  </button>
                );
              })}
            </div>
          </CardContent>
        </Card>

        <div className="space-y-4">
          <Card>
            <CardHeader className="pb-3">
              <CardTitle className="text-sm font-medium">
                {selectedDate ? `${selectedDate} ${MONTHS[currentMonth]} ${currentYear}` : 'Yaklaşan Etkinlikler'}
              </CardTitle>
            </CardHeader>
            <CardContent>
              {selectedDate && selectedEvents.length > 0 ? (
                <div className="space-y-3">
                  {selectedEvents.map((event) => {
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
                    UPCOMING_EVENTS.map((event) => <UpcomingEvent key={event.id} event={event} />)
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

export default memo(CalendarPageComponent);
