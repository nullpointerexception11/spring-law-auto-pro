import React, { useState, useRef, useEffect } from 'react';
import { Bot, User, Send, Loader2, Sparkles, BrainCircuit, History, Info } from 'lucide-react';
import { aiService } from '@/api/aiService';
import { cn } from '@/lib/utils';
import ReactMarkdown from 'react-markdown';
import { motion, AnimatePresence } from 'framer-motion';

export default function AiAssistantPage() {
  const [messages, setMessages] = useState([
    { 
      id: 1, 
      role: 'assistant', 
      content: '# Merhaba! Ben LawAuto Yapay Zeka Asistanı.\n\nSize dava süreçleri, mevzuat araştırmaları veya doküman özetleme konularında yardımcı olabilirim. Ne sormak istersiniz?' 
    }
  ]);
  const [input, setInput] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [currentStreamedMessage, setCurrentStreamedMessage] = useState('');
  const [conversationId] = useState(() => crypto.randomUUID()); // Stable ID for session
  const scrollRef = useRef(null);
  const abortControllerRef = useRef(null);

  useEffect(() => {
    return () => {
      if (abortControllerRef.current) {
        abortControllerRef.current.abort();
      }
    };
  }, []);

  useEffect(() => {
    if (scrollRef.current) {
      scrollRef.current.scrollTop = scrollRef.current.scrollHeight;
    }
  }, [messages, currentStreamedMessage, isLoading]);

  const handleSend = async () => {
    if (!input.trim() || isLoading) return;

    // Cancel any existing request
    if (abortControllerRef.current) {
      abortControllerRef.current.abort();
    }
    abortControllerRef.current = new AbortController();

    const userMessage = { id: crypto.randomUUID(), role: 'user', content: input };
    setMessages(prev => [...prev, userMessage]);
    const promptText = input;
    setInput('');
    setIsLoading(true);
    setCurrentStreamedMessage('');

    try {
      let accumulatedResponse = '';
      await aiService.chatStream(
        promptText, 
        (chunk) => {
          accumulatedResponse += chunk;
          setCurrentStreamedMessage(accumulatedResponse);
        },
        abortControllerRef.current.signal,
        conversationId
      );

      setMessages(prev => [...prev, { 
        id: crypto.randomUUID(), 
        role: 'assistant', 
        content: accumulatedResponse 
      }]);
      setCurrentStreamedMessage('');
    } catch (error) {
      if (error.name === 'AbortError') return;
      
      setMessages(prev => [...prev, { 
        id: crypto.randomUUID(), 
        role: 'assistant', 
        content: '**Hata:** Sunucuyla bağlantı kurulamadı veya istek iptal edildi.' 
      }]);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="flex flex-col h-[calc(100vh-120px)] bg-slate-50/50 rounded-3xl border border-slate-200 shadow-sm overflow-hidden fade-enter-active">
      {/* Header */}
      <div className="bg-white px-8 py-5 border-b border-slate-200 flex items-center justify-between shadow-sm">
        <div className="flex items-center gap-4">
          <div className="h-12 w-12 rounded-2xl bg-indigo-600 flex items-center justify-center shadow-lg shadow-indigo-100">
            <BrainCircuit className="h-7 w-7 text-white" />
          </div>
          <div>
            <h1 className="text-xl font-bold text-slate-900">Yapay Zeka Hukuk Danışmanı</h1>
            <p className="text-xs text-slate-500 font-medium">Spring AI & GPT-4o ile güçlendirilmiş gelişmiş asistan</p>
          </div>
        </div>
        <div className="flex items-center gap-2">
          <button className="p-2.5 rounded-xl hover:bg-slate-100 text-slate-500 transition-all" title="Geçmiş">
            <History className="h-5 w-5" />
          </button>
          <button className="p-2.5 rounded-xl hover:bg-slate-100 text-slate-500 transition-all" title="Bilgi">
            <Info className="h-5 w-5" />
          </button>
        </div>
      </div>

      {/* Messages */}
      <div 
        ref={scrollRef}
        className="flex-1 overflow-y-auto p-8 space-y-8 scroll-smooth"
      >
        <div className="max-w-4xl mx-auto w-full space-y-8">
          {messages.map((msg) => (
            <motion.div
              initial={{ opacity: 0, y: 10 }}
              animate={{ opacity: 1, y: 0 }}
              key={msg.id}
              className={cn(
                "flex gap-6",
                msg.role === 'user' ? "flex-row-reverse" : ""
              )}
            >
              <div className={cn(
                "h-10 w-10 rounded-2xl flex items-center justify-center shrink-0 shadow-sm",
                msg.role === 'user' ? "bg-indigo-600 text-white" : "bg-white border border-slate-200 text-indigo-600"
              )}>
                {msg.role === 'user' ? <User className="h-5 w-5" /> : <Bot className="h-5 w-5" />}
              </div>
              <div className={cn(
                "p-6 rounded-3xl text-sm leading-relaxed shadow-sm min-w-[100px] max-w-[85%]",
                msg.role === 'user' 
                  ? "bg-indigo-600 text-white rounded-tr-none" 
                  : "bg-white border border-slate-100 text-slate-700 rounded-tl-none"
              )}>
                <ReactMarkdown className={cn(
                  "prose prose-sm max-w-none",
                  msg.role === 'user' ? "prose-invert" : "prose-slate"
                )}>
                  {msg.content}
                </ReactMarkdown>
              </div>
            </motion.div>
          ))}

          {/* Current Streamed Message */}
          <AnimatePresence>
            {currentStreamedMessage && (
              <motion.div
                initial={{ opacity: 0, y: 10 }}
                animate={{ opacity: 1, y: 0 }}
                className="flex gap-6"
              >
                <div className="h-10 w-10 rounded-2xl bg-white border border-slate-200 text-indigo-600 flex items-center justify-center shrink-0 shadow-sm">
                  <Bot className="h-5 w-5" />
                </div>
                <div className="p-6 rounded-3xl rounded-tl-none text-sm leading-relaxed max-w-[85%] bg-white border border-slate-100 text-slate-700 shadow-sm">
                  <ReactMarkdown className="prose prose-sm max-w-none prose-slate text-slate-700">
                    {currentStreamedMessage}
                  </ReactMarkdown>
                </div>
              </motion.div>
            )}
          </AnimatePresence>

          {isLoading && !currentStreamedMessage && (
            <div className="flex gap-6">
              <div className="h-10 w-10 rounded-2xl bg-white border border-slate-200 text-indigo-600 flex items-center justify-center shrink-0 shadow-sm">
                <Loader2 className="h-5 w-5 animate-spin" />
              </div>
              <div className="p-6 rounded-3xl rounded-tl-none text-sm bg-white border border-slate-100 text-slate-400 italic shadow-sm">
                Asistan yanıtını hazırlıyor...
              </div>
            </div>
          )}
        </div>
      </div>

      {/* Input Area */}
      <div className="p-8 bg-white border-t border-slate-100">
        <div className="max-w-4xl mx-auto relative flex items-center gap-4">
          <div className="relative flex-1">
            <textarea
              rows={1}
              placeholder="Hukuki analiz, doküman özeti veya dava stratejisi sorun..."
              value={input}
              onChange={(e) => setInput(e.target.value)}
              onKeyDown={(e) => {
                if (e.key === 'Enter' && !e.shiftKey) {
                  e.preventDefault();
                  handleSend();
                }
              }}
              disabled={isLoading}
              className="w-full bg-slate-50 border border-slate-200 rounded-3xl py-5 px-8 pr-16 text-sm focus:outline-none focus:ring-4 focus:ring-indigo-500/10 focus:bg-white focus:border-indigo-300 transition-all disabled:opacity-50 resize-none min-h-[64px] max-h-[200px]"
            />
            <button
              onClick={handleSend}
              disabled={isLoading || !input.trim()}
              className="absolute right-3 top-3 h-11 w-11 rounded-2xl bg-indigo-600 text-white flex items-center justify-center hover:bg-indigo-700 disabled:opacity-30 disabled:hover:bg-indigo-600 transition-all shadow-lg shadow-indigo-100"
            >
              <Send className="h-5 w-5" />
            </button>
          </div>
        </div>
        <p className="max-w-4xl mx-auto text-[10px] text-slate-400 mt-4 text-center">
          Yapay zeka hatalar yapabilir. Önemli hukuki kararlar vermeden önce bilgileri doğrulayın.
        </p>
      </div>
    </div>
  );
}
