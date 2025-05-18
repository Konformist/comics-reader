import { registerPlugin, WebPlugin } from '@capacitor/core';
import { Directory, Filesystem } from '@capacitor/filesystem';

interface IResizeOptions {
  path: string
  width?: number
  height?: number
}

interface IOptimizationOptions {
  path: string
  data: string
}

interface IImageFuck {
  resize(options: IResizeOptions): Promise<void>
  optimization(options: IOptimizationOptions): Promise<void>
}

class ImageFuckPlugin extends WebPlugin implements IImageFuck {
  async resize(options: IResizeOptions): Promise<void> {
    console.log(options);
  }

  async optimization(options: IOptimizationOptions): Promise<void> {
    await Filesystem.writeFile({
      path: options.path,
      directory: Directory.Data,
      data: options.data,
    });
  }
}

const ImageFuck = registerPlugin<IImageFuck>('ImageFuck', ImageFuckPlugin);

export default ImageFuck;
