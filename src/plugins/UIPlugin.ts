import { registerPlugin } from '@capacitor/core';

interface IUIPlugin {
  toast: (option: { text: string }) => Promise<void>
  loading: (option: { show: boolean }) => Promise<void>
  reading: (option: { mode: 'start' | 'end' }) => Promise<void>
}

const UI = registerPlugin<IUIPlugin>('UI');

export default UI;
