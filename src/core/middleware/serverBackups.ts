import { Directory, Encoding, Filesystem } from '@capacitor/filesystem';
import serverComics from '@/core/middleware/serverComics.ts';
import serverParsers from '@/core/middleware/serverParsers.ts';
import serverSettings from '@/core/middleware/serverSettings.ts';
// import serverTags from '@/core/middleware/serverTags.ts';
import {
  BACKUPS_DIRECTORY,
  COMICS_VERSION,
  PARSERS_VERSION,
  // TAGS_VERSION,
} from '@/core/middleware/variables.ts';

const getBackupFileName = () => {
  const now = new Date();
  const year = now.getFullYear();
  const month = (now.getMonth() + 1).toString().padStart(2, '0');
  const day = (now.getDate()).toString().padStart(2, '0');

  return `${year}-${month}-${day}.json`;
};

const setBackup = async (): Promise<void> => {
  await serverSettings.getSettingsData();
  // await serverTags.getTagsData();
  await serverComics.getComicsData();
  await serverParsers.getParsersData();

  await Filesystem.writeFile({
    path: `${BACKUPS_DIRECTORY}/${getBackupFileName()}`,
    directory: Directory.Data,
    encoding: Encoding.UTF8,
    recursive: true,
    data: JSON.stringify({
      settings: serverSettings.settingsRaw,
      parsers: {
        version: PARSERS_VERSION,
        items: serverParsers.parsersRaw,
      },
      // tags: {
      //   version: TAGS_VERSION,
      //   items: serverTags.tagsRaw,
      // },
      comics: {
        version: COMICS_VERSION,
        items: serverComics.comicsRaw,
      },
    }),
  });
};

const autoBackup = async () => {
  try {
    await Filesystem.readFile({
      path: `${BACKUPS_DIRECTORY}/${getBackupFileName()}`,
      directory: Directory.Data,
    });
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
  } catch (_) {
    await setBackup();
  }
};

const getBackup = async (path: string): Promise<void> => {
  const result = await Filesystem.readFile({
    path,
    directory: Directory.Data,
    encoding: Encoding.UTF8,
  });

  if (!result) return;

  const parsedData = JSON.parse(result.data as string);

  serverSettings.settingsRaw = parsedData.settings?.item ?? null;
  await serverSettings.setSettingsData();

  serverParsers.parsersRaw = parsedData.parsers?.items || [];
  await serverParsers.setParsersData();

  // serverTags.tagsRaw = parsedData.tags?.items || [];
  // await serverTags.setTagsData();

  serverComics.comicsRaw = parsedData.comics?.items || [];
  await serverComics.setComicsData();
};

export default {
  autoBackup,
  setBackup,
  getBackup,
};
