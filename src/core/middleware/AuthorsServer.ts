import type { IAuthorDTO } from '@/core/object-value/author/AuthorTypes.ts';
import ServerAbstract from '@/core/middleware/ServerAbstract.ts';
import ComicsServer from '@/core/middleware/ComicsServer.ts';
import { AUTHORS_STORE } from '@/core/middleware/variables.ts';

class AuthorsServer extends ServerAbstract<IAuthorDTO> {
  constructor() {
    super(AUTHORS_STORE);
  }

  public async getItems(): Promise<IAuthorDTO[]> {
    await this.getDatabase();

    return [...this.dataRaw];
  }

  public async getItem(id: number): Promise<IAuthorDTO | undefined> {
    const ids = this.dataRaw.map((e) => e.id);

    if (!ids.includes(id)) await this.getDatabase();

    return this.dataRaw.find((e) => e.id === id);
  }

  public async addItem(value: IAuthorDTO): Promise<number> {
    await this.getDatabase();

    const newId = this.getNewId();
    this.dataRaw.push({ ...value, id: newId });
    await this.setDatabase();

    return newId;
  }

  public async setItem(value: IAuthorDTO): Promise<void> {
    const item = await this.getItem(value.id);

    if (!item) return;

    Object.assign(item, value);
    await this.setDatabase();
  }

  public async delItem(id: number): Promise<void> {
    const item = await this.getItem(id);

    if (!item) return;

    this.dataRaw = this.dataRaw.filter((e) => e.id !== id);

    await ComicsServer.getDatabase();
    ComicsServer.dataRaw.forEach((comic) => {
      comic.authors = comic.authors.filter((e) => e !== id);
    });
    await this.setDatabase();
    await ComicsServer.setDatabase();
  }
}

export default new AuthorsServer();
