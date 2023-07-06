# Dcm4che Transcode Issue

This repository was created to demonstrate error trying to convert dicom file to original transfer syntax using dcm4che library.

- Issue: https://github.com/dcm4che/dcm4che/issues/1323
- PR with solution: https://github.com/dcm4che/dcm4che/pull/1324

## How to execute?

- Execute the main file: org.example.Application
- The error will be thrown in the terminal

## How to simulate?

1. Given file with transfer syntax: `1.2.840.10008.1.2.4.70`
2. Transcode file to `1.2.840.10008.1.2.4.80`
3. When trying to return the file created in second step (2) to the original transfer syntax (`1.2.840.10008.1.2.4.70`), the following error occurs:

```log
java.lang.ClassCastException: java.awt.image.DataBufferByte cannot be cast to java.awt.image.DataBufferShort
	at org.dcm4che3.image.BufferedImageUtils.convertShortsToBytes(BufferedImageUtils.java:169) ~[dcm4che-image-5.24.0.jar:5.24.0]
	at org.dcm4che3.imageio.codec.Transcoder.compressPixelData(Transcoder.java:579) ~[dcm4che-imageio-5.24.0.jar:5.24.0]
	at org.dcm4che3.imageio.codec.Transcoder.processPixelData(Transcoder.java:507) ~[dcm4che-imageio-5.24.0.jar:5.24.0]
	at org.dcm4che3.imageio.codec.Transcoder.access$800(Transcoder.java:71) ~[dcm4che-imageio-5.24.0.jar:5.24.0]
	at org.dcm4che3.imageio.codec.Transcoder$1.readValue(Transcoder.java:464) ~[dcm4che-imageio-5.24.0.jar:5.24.0]
	at org.dcm4che3.io.DicomInputStream.readAttributes(DicomInputStream.java:576) ~[dcm4che-core-5.24.0.jar:5.24.0]
	at org.dcm4che3.io.DicomInputStream.readAllAttributes(DicomInputStream.java:483) ~[dcm4che-core-5.24.0.jar:5.24.0]
	at org.dcm4che3.imageio.codec.Transcoder.transcode(Transcoder.java:438) ~[dcm4che-imageio-5.24.0.jar:5.24.0]
	at org.example.Application.transcode(Application.java:63) [classes/:?]
	at org.example.Application.main(Application.java:46) [classes/:?]
```
