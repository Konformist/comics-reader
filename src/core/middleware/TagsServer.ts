import type { ITagDTO } from '@/core/object-value/tag/TagTypes.ts';
import ServerAbstract from '@/core/middleware/ServerAbstract.ts';
import ComicsServer from '@/core/middleware/ComicsServer.ts';
import { TAGS_STORE } from '@/core/middleware/variables.ts';

class TagsServer extends ServerAbstract<ITagDTO> {
  constructor() {
    super(TAGS_STORE);
  }

  public async getItems(): Promise<ITagDTO[]> {
    await this.getDatabase();

    return this.dataRaw.map((e) => ({ ...e }));
  }

  public async getItem(id: number): Promise<ITagDTO | undefined> {
    const ids = this.dataRaw.map((e) => e.id);

    if (!ids.includes(id)) await this.getDatabase();

    const result = this.dataRaw.find((e) => e.id === id);

    if (!result) return;

    return { ...result };
  }

  public async addItem(value: ITagDTO): Promise<number> {
    await this.getDatabase();

    const newId = this.getNewId();
    this.dataRaw.push({ ...value, id: newId });
    await this.setDatabase();

    return newId;
  }

  public async setItem(value: ITagDTO): Promise<void> {
    const item = await this.getItem(value.id);

    if (!item) return;

    const index = this.dataRaw.findIndex((e) => e.id === value.id);
    this.dataRaw.splice(index, 1, value);
    await this.setDatabase();
  }

  public async delItem(id: number): Promise<void> {
    const item = await this.getItem(id);

    if (!item) return;

    this.dataRaw = this.dataRaw.filter((e) => e.id !== id);

    await ComicsServer.getDatabase();
    ComicsServer.dataRaw.forEach((comic) => {
      comic.tags = comic.tags.filter((e) => e !== id);
    });
    await this.setDatabase();
    await ComicsServer.setDatabase();
  }
}

export default new TagsServer();
