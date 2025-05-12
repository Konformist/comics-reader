export interface IFile {
  type: 'file';
  path: string;
  size: number;
}

export interface IDirectory {
  type: 'directory';
  path: string;
  children: Array<IFile | IDirectory>;
}
