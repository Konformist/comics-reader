export interface ITreeFile {
  type: 'file';
  path: string;
  name: string;
  size: number;
}

export interface ITreeDirectory {
  type: 'directory';
  path: string;
  name: string;
  children: Array<ITreeFile | ITreeDirectory>;
}

export interface IFileDTO {
  /** ID файла */
  id: number
  /** Имя файла */
  name: string
  /** Mime тип */
  mime: string
  /** Размер */
  size: number
  /** Дата изменения */
  mdate: number
  /** Дата создания */
  cdate: number
  /** Путь в файловой системе */
  path: string
}
