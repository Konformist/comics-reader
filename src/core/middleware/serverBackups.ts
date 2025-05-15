import AuthorsServer from '@/core/middleware/AuthorsServer.ts';
import LanguagesServer from '@/core/middleware/LanguagesServer.ts';
import migrator from '@/core/middleware/migrator.ts';
import serverFiles from '@/core/middleware/serverFiles.ts';
import { Directory, Filesystem } from '@capacitor/filesystem';
import serverSettings from '@/core/middleware/serverSettings.ts';
import ComicsServer from '@/core/middleware/ComicsServer.ts';
import ParsersServer from '@/core/middleware/ParsersServer.ts';
import TagsServer from '@/core/middleware/TagsServer.ts';
import { BACKUPS_DIRECTORY } from '@/core/middleware/variables.ts';

const getBackupFileName = () => {
  const now = new Date();
  const year = now.getFullYear();
  const month = (now.getMonth() + 1).toString().padStart(2, '0');
  const day = (now.getDate()).toString().padStart(2, '0');

  return `${year}-${month}-${day}.json`;
};

const addBackup = async (): Promise<void> => {
  await serverSettings.getSettingsData();
  await AuthorsServer.getDatabase();
  await LanguagesServer.getDatabase();
  await TagsServer.getDatabase();
  await ComicsServer.getDatabase();
  await ParsersServer.getDatabase();

  await serverFiles.addFile(`${BACKUPS_DIRECTORY}/${getBackupFileName()}`, JSON.stringify({
    version: migrator.dataRaw.item,
    settings: serverSettings.dataRaw.item,
    parsers: ParsersServer.dataRaw,
    authors: AuthorsServer.dataRaw,
    languages: LanguagesServer.dataRaw,
    tags: TagsServer.dataRaw,
    comics: ComicsServer.dataRaw,
  }));
};

const delBackup = (path: string): Promise<void> => (serverFiles.delFile(path));

const getBackup = async (path: string): Promise<void> => {
  const result = await serverFiles.getFile(path, 'string');

  if (!result) return;

  const parsedData = JSON.parse(result);

  // @todo for old backups
  if (!parsedData.version) {
    parsedData.settings = parsedData.settings?.item;
    parsedData.parsers = parsedData.parsers?.items;
    parsedData.comics = parsedData.comics?.items;
  }

  if (parsedData.version) {
    migrator.dataRaw.item = parsedData.version;
    await migrator.setVersionData();
  }

  if (parsedData.settings) {
    serverSettings.dataRaw.item = parsedData.settings;
    await serverSettings.setSettingsData();
  }

  if (parsedData.parsers) {
    ParsersServer.dataRaw = parsedData.parsers;
    await ParsersServer.setDatabase();
  }

  if (parsedData.languages) {
    LanguagesServer.dataRaw = parsedData.languages;
    await LanguagesServer.setDatabase();
  }

  if (parsedData.authors) {
    AuthorsServer.dataRaw = parsedData.authors;
    await AuthorsServer.setDatabase();
  }

  if (parsedData.tags) {
    TagsServer.dataRaw = parsedData.tags;
    await TagsServer.setDatabase();
  }

  if (parsedData.comics) {
    ComicsServer.dataRaw = parsedData.comics;
    await ComicsServer.setDatabase();
  }
};

const autoBackup = async () => {
  try {
    await Filesystem.readFile({
      path: `${BACKUPS_DIRECTORY}/${getBackupFileName()}`,
      directory: Directory.Data,
    });
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
  } catch (_) {
    await addBackup();
  }
};

export default {
  autoBackup,
  addBackup,
  delBackup,
  getBackup,
};
