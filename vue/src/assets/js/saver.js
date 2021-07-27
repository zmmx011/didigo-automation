import {saveAs} from 'file-saver';

const saveFile = (response, defaultFileName) => {
  const fileNameHeader = 'content-disposition';
  const suggestedFileName = response.headers[fileNameHeader];
  const effectiveFileName = (suggestedFileName === undefined ? defaultFileName
      : suggestedFileName.split('"')[1]);
  console.log('Received header [' + fileNameHeader + ']: ' + suggestedFileName
      + ', effective fileName: ' + effectiveFileName);

  saveAs(response.data, effectiveFileName);
}
export {saveFile}
