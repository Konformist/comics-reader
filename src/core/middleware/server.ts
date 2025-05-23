import migrator from '@/core/middleware/migrator.ts';
import serverSettings from '@/core/middleware/serverSettings.ts';
import FilesServer from '@/core/middleware/FilesServer.ts';

export default {
  migrate: migrator.migrate,
  getTree: (p: string) => FilesServer.getTree(p),
  setSettings: serverSettings.setSettings,
  getSettings: serverSettings.getSettings,
};
