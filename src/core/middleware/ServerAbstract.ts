import { Preferences } from '@capacitor/preferences';

export interface IDataRaw {
  id: number
}

export default abstract class ServerAbstract<T extends IDataRaw> {
  dataRaw: Array<T> = [];
  storeName: string = '';

  constructor(storeName: string) {
    this.storeName = storeName;
  }

  getNewId(): number {
    return Math.max(...this.dataRaw.map((e) => e.id), 0) + 1;
  }

  async getDatabase(): Promise<void> {
    const result = await Preferences.get({ key: this.storeName });

    if (!result.value) await this.setDatabase();
    else this.dataRaw = JSON.parse(result.value);
  }

  setDatabase(): Promise<void> {
    return Preferences.set({
      key: this.storeName,
      value: JSON.stringify(this.dataRaw),
    });
  }

  abstract getItems(): Promise<T[]>;
  abstract getItem(id: number): Promise<T | undefined>;
  abstract addItem(value: T): Promise<number>;
  abstract setItem(value: T): Promise<void>;
  abstract delItem(id: number): Promise<void>;
}
