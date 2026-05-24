import{r as n,c as f,V as i,a as d,e as p,o as y,b as S}from"./vendor-D4-Wtrtv.js";/**
   * react-table
   *
   * Copyright (c) TanStack
   *
   * This source code is licensed under the MIT license found in the
   * LICENSE.md file in the root directory of this source tree.
   *
   * @license MIT
   */function g(e,t){return e?b(e)?n.createElement(e,t):e:null}function b(e){return E(e)||typeof e=="function"||h(e)}function E(e){return typeof e=="function"&&(()=>{const t=Object.getPrototypeOf(e);return t.prototype&&t.prototype.isReactComponent})()}function h(e){return typeof e=="object"&&typeof e.$$typeof=="symbol"&&["react.memo","react.forward_ref"].includes(e.$$typeof.description)}function O(e){const t={state:{},onStateChange:()=>{},renderFallbackValue:null,...e},[r]=n.useState(()=>({current:f(t)})),[o,a]=n.useState(()=>r.current.initialState);return r.current.setOptions(u=>({...u,...e,state:{...o,...e.state},onStateChange:s=>{a(s),e.onStateChange==null||e.onStateChange(s)}})),r.current}const l=typeof document<"u"?n.useLayoutEffect:n.useEffect;function m({useFlushSync:e=!0,...t}){const r=n.useReducer(()=>({}),{})[1],o={...t,onChange:(u,s)=>{var c;e&&s?d.flushSync(r):r(),(c=t.onChange)==null||c.call(t,u,s)}},[a]=n.useState(()=>new i(o));return a.setOptions(o),l(()=>a._didMount(),[]),l(()=>a._willUpdate()),a}function R(e){return m({observeElementRect:S,observeElementOffset:y,scrollToFn:p,...e})}export{R as a,g as f,O as u};
