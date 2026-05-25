import React, { useState, useRef, useEffect, useCallback } from 'react';
import { Send, User, Bot } from 'lucide-react';
import { aiV2Service } from '@/api/aiV2Service';
import ReactMarkdown from 'react-markdown';

export default function AiAssistantPageV2() {
  const [messages, setMessages] = useState([
    {
      id: 'welcome',
      role: 'assistant',
      content: 'Merhaba! Ben LawAuto Gelişmiş Yapay Zeka Asistanı.\n\nRAG, tool calling ve akıllı model yönlendirme ile güçlendirilmiştir.'
    }
  ]);
  const [input, setInput] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [conversationId] = useState(() => crypto.randomUUID());
  const scrollRef = useRef(null);

  useEffect(() => {
    scrollRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  const handleSend = useCallback(async () => {
    if (!input.trim() || isLoading) return;
    const text = input;
    setInput('');
    setMessages(prev => [...prev, { id: crypto.randomUUID(), role: 'user', content: text }]);
    setIsLoading(true);

    try {
      const response = await aiV2Service.chat(text, conversationId);
      const reply = typeof response === 'string' ? response : response.reply || JSON.stringify(response);
      setMessages(prev => [...prev, { id: crypto.randomUUID(), role: 'assistant', content: reply }]);
    } catch {
      setMessages(prev => [...prev, {
        id: crypto.randomUUID(),
        role: 'assistant',
        content: 'Bağlantı hatası. Lütfen daha sonra tekrar deneyin.'
      }]);
    } finally {
      setIsLoading(false);
    }
  }, [input, isLoading, conversationId]);

  return (
    <div className="flex flex-col h-[calc(100vh-120px)] rounded-lg border border-border bg-card">
      <div className="flex items-center gap-3 px-5 py-3.5 border-b border-border">
        <div className="w-8 h-8 rounded-md bg-primary flex items-center justify-center">
          <Bot className="w-4 h-4 text-primary-foreground" />
        </div>
        <div>
          <h2 className="text-sm font-medium text-foreground">AI Asistan v2</h2>
          <p className="text-xs text-muted-foreground">RAG + Tool Calling + Model Routing</p>
        </div>
      </div>

      <div className="flex-1 overflow-y-auto px-5 py-5">
        <div className="max-w-2xl mx-auto space-y-5">
          {messages.map((msg) => (
            <div key={msg.id} className={`flex gap-3 ${msg.role === 'user' ? 'flex-row-reverse' : ''}`}>
              <div className={`w-7 h-7 rounded-md flex items-center justify-center shrink-0 ${
                msg.role === 'user' ? 'bg-primary' : 'bg-muted'
              }`}>
                {msg.role === 'user' ? (
                  <User className="w-3.5 h-3.5 text-primary-foreground" />
                ) : (
                  <Bot className="w-3.5 h-3.5 text-muted-foreground" />
                )}
              </div>
              <div className={`px-4 py-2.5 rounded-md text-sm leading-relaxed max-w-[75%] ${
                msg.role === 'user'
                  ? 'bg-primary text-primary-foreground'
                  : 'bg-muted text-foreground'
              }`}>
                <ReactMarkdown>{msg.content}</ReactMarkdown>
              </div>
            </div>
          ))}

          {isLoading && (
            <div className="flex gap-3">
              <div className="w-7 h-7 rounded-md bg-muted flex items-center justify-center shrink-0">
                <Bot className="w-3.5 h-3.5 text-muted-foreground" />
              </div>
              <div className="px-4 py-2.5 rounded-md text-sm bg-muted text-muted-foreground italic">
                Yanıt hazırlanıyor...
              </div>
            </div>
          )}

          <div ref={scrollRef} />
        </div>
      </div>

      <div className="px-5 py-3.5 border-t border-border">
        <div className="max-w-2xl mx-auto flex gap-2.5">
          <input
            type="text"
            placeholder="Hukuki bir konu sorun..."
            value={input}
            onChange={(e) => setInput(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && !e.shiftKey && handleSend()}
            disabled={isLoading}
            className="flex-1 h-9 px-3 rounded-md border border-input bg-background text-sm focus:outline-none focus:ring-1 focus:ring-ring disabled:opacity-50"
          />
          <button
            onClick={handleSend}
            disabled={isLoading || !input.trim()}
            className="h-9 px-3 rounded-md bg-primary text-primary-foreground text-xs font-medium hover:bg-primary/90 disabled:opacity-50 transition-colors"
          >
            <Send className="w-3.5 h-3.5" />
          </button>
        </div>
      </div>
    </div>
  );
}
