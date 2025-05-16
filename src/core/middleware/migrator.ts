import type { IComicImageUrl } from '@/core/entities/comic/ComicTypes.ts';
import ComicsServer from '@/core/middleware/ComicsServer.ts';
import FilesServer from '@/core/middleware/FilesServer.ts';
import type { IFileDTO } from '@/core/object-value/file/FileTypes.ts';
import { Preferences } from '@capacitor/preferences';
import { COMICS_FILES_DIRECTORY, DATABASE_STORE, DATABASE_VERSION } from '@/core/middleware/variables.ts';

const dataRaw: { item: number } = {
  item: 0,
};

const setVersionData = () => (
  Preferences.set({
    key: DATABASE_STORE,
    value: dataRaw.item.toString(),
  })
);

const getVersionData = async () => {
  const result = await Preferences.get({ key: DATABASE_STORE });

  if (!result.value) {
    dataRaw.item = DATABASE_VERSION;
    await setVersionData();
  } else {
    dataRaw.item = +result.value;
  }
};

const setVersion = async (value: number) => {
  dataRaw.item = value;
  await setVersionData();
};

const getVersion = async () => {
  if (!dataRaw.item) await getVersionData();

  return dataRaw.item;
};

const migrate = async () => {
  await getVersionData();

  if (dataRaw.item === DATABASE_VERSION) return;

  dataRaw.item = DATABASE_VERSION;
  await ComicsServer.getDatabase();

  for (const comic of ComicsServer.dataRaw) {
    comic.image = {
      id: 0,
      fileId: 0,
      // @ts-expect-error fuck
      url: comic.imageUrl,
    };

    if (comic.image) {
      const coverPath = `${COMICS_FILES_DIRECTORY}/${comic.id}/cover.webp`;
      const coverStat = await FilesServer.getFileStat(coverPath);
      const newCover: IFileDTO = {
        id: FilesServer.getNewId(),
        name: 'cover.webp',
        mime: 'image/webp',
        size: coverStat.size,
        cdate: coverStat.cdate,
        mdate: coverStat.mdate,
        path: coverPath,
      };
      FilesServer.dataRaw.push(newCover);
      comic.image.fileId = newCover.id;
    }

    const newImages: IComicImageUrl[] = [];

    for (const image of comic.images) {
      const newImage = {
        id: image.id,
        // @ts-expect-error fuck
        url: image.from,
        fileId: 0,
      };

      newImages.push(newImage);

      if (!image.url) continue;

      const imagePath = `${COMICS_FILES_DIRECTORY}/${comic.id}/${image.id}.webp`;
      const imageStat = await FilesServer.getFileStat(imagePath);
      const newImageFile: IFileDTO = {
        id: FilesServer.getNewId(),
        name: `${image.id}.webp`,
        mime: 'image/webp',
        size: imageStat.size,
        cdate: imageStat.cdate,
        mdate: imageStat.mdate,
        path: imagePath,
      };
      FilesServer.dataRaw.push(newImageFile);
      newImage.fileId = newImageFile.id;
    }

    comic.images = newImages;
  }

  await ComicsServer.setDatabase();
  await FilesServer.setDatabase();
  await setVersionData();
};

export default {
  migrate,
  dataRaw,
  setVersionData,
  getVersionData,
  setVersion,
  getVersion,
};
