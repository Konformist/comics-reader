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
  id: number
  name: string
  mime: string
  size: number
  mdate: number
  cdate: number
  path: string
}
