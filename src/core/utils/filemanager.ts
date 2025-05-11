export interface IFile {
  type: 'file'
  path: string
}

export interface IDirectory {
  type: 'directory'
  path: string
  children: Array<IFile|IDirectory>
}
