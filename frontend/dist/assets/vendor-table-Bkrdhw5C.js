import{r,p as f,V as i,q as d,t as p,u as y}from"./vendor-pXXAs3A1.js";import{r as S}from"./vendor-react-duW5JTmU.js";/**
   * react-table
   *
   * Copyright (c) TanStack
   *
   * This source code is licensed under the MIT license found in the
   * LICENSE.md file in the root directory of this source tree.
   *
   * @license MIT
   */function O(e,t){return e?m(e)?r.createElement(e,t):e:null}function m(e){return b(e)||typeof e=="function"||E(e)}function b(e){return typeof e=="function"&&(()=>{const t=Object.getPrototypeOf(e);return t.prototype&&t.prototype.isReactComponent})()}function E(e){return typeof e=="object"&&typeof e.$$typeof=="symbol"&&["react.memo","react.forward_ref"].includes(e.$$typeof.description)}function R(e){const t={state:{},onStateChange:()=>{},renderFallbackValue:null,...e},[n]=r.useState(()=>({current:f(t)})),[o,a]=r.useState(()=>n.current.initialState);return n.current.setOptions(u=>({...u,...e,state:{...o,...e.state},onStateChange:s=>{a(s),e.onStateChange==null||e.onStateChange(s)}})),n.current}const l=typeof document<"u"?r.useLayoutEffect:r.useEffect;function h({useFlushSync:e=!0,...t}){const n=r.useReducer(()=>({}),{})[1],o={...t,onChange:(u,s)=>{var c;e&&s?S.flushSync(n):n(),(c=t.onChange)==null||c.call(t,u,s)}},[a]=r.useState(()=>new i(o));return a.setOptions(o),l(()=>a._didMount(),[]),l(()=>a._willUpdate()),a}function v(e){return h({observeElementRect:y,observeElementOffset:p,scrollToFn:d,...e})}export{v as a,O as f,R as u};
