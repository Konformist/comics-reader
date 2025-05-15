import ServerAbstract from '@/core/middleware/ServerAbstract.ts';
import ComicsServer from '@/core/middleware/ComicsServer.ts';
import { LANGUAGES_STORE } from '@/core/middleware/variables.ts';
import type { ILanguageDTO } from '@/core/object-value/language/LanguageTypes.ts';

class LanguagesServer extends ServerAbstract<ILanguageDTO> {
  constructor() {
    super(LANGUAGES_STORE);
  }

  public async getItems(): Promise<ILanguageDTO[]> {
    await this.getDatabase();

    return [...this.dataRaw];
  }

  public async getItem(id: number): Promise<ILanguageDTO | undefined> {
    const ids = this.dataRaw.map((e) => e.id);

    if (!ids.includes(id)) await this.getDatabase();

    return this.dataRaw.find((e) => e.id === id);
  }

  public async addItem(value: ILanguageDTO): Promise<number> {
    await this.getDatabase();

    const newId = this.getNewId();
    this.dataRaw.push({ ...value, id: newId });
    await this.setDatabase();

    return newId;
  }

  public async setItem(value: ILanguageDTO): Promise<void> {
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
      if (comic.language === id) comic.language = 0;
    });
    await this.setDatabase();
    await ComicsServer.setDatabase();
  }
}

export default new LanguagesServer();
