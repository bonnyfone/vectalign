# VectAlign [ ![Download](https://api.bintray.com/packages/bonnyfone/maven/org.bonnyfone.vectalign/images/download.svg) ](https://bintray.com/artifact/download/bonnyfone/maven/vectalign-0.2-jar-with-dependencies.jar) [![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-VectAlign-brightgreen.svg?style=flat)](http://android-arsenal.com/details/1/2443)

**VectAlign** (a.k.a. *VectorDrawableAlign*) is a developer's tool which automagically **aligns two `VectorDrawable` "pathData" strings (or SVG images) in order to allow morphing animations** between them using an `AnimatedVectorDrawable`. VectAlign can be used from command line or through a simple GUI (which includes *morphing preview* and *export* functionalities).


<img src="http://s33.postimg.org/qiaafk7xb/vectalign_0_2_demo.gif" width="530px">

Here are some examples of what you can do with the help of VectAlign (take a look at the [sample project] for a live demo):

<img src="http://s4.postimg.org/boxc1zk0p/morph2.gif" width="110px">
<img src="http://s21.postimg.org/4657b7m0j/morph1.gif" width="110px">
<img src="http://s28.postimg.org/8mdcxb48t/morph5.gif" width="110px">
<img src="http://s18.postimg.org/79coo8vid/morph3.gif" width="120px">
<img src="http://s9.postimg.org/a5tdgfppn/morph4.gif" width="140px">

 The problem
--

As stated in the [official docs] two paths must be *compatible* so that they can be morphed, which means that the **paths must be composed by the same list of SVG commands** (in terms of length and type of commands). 

Example of **compatible paths**: 
```a
M 10,10 L 40,10 L 40,40 L 10,40 Z
M 25,10 L 40,25 L 25,40 L 10,25 Z
```

Example of **incompatible paths**: 
```
M 10,10 L 40,10 L 40,40 L 10,40 Z
M 30,30 L 10,10 C 14,25 20,30 10,49 L 3,3 L 0,8 Z
```

Creating an `AnimatedVectorDrawable` containing morphing animations which use *incompatible* paths leads to runtime exceptions like the following:

```
android.view.InflateException: Binary XML file line #3 Can't morph from M 10,10 L 40,10 L 40,40 L 10,40 Z  to  M 30,30 L 10,10 C 14,25 20,30 10,49 L 3,3 L 0,8 Z
        at android.animation.AnimatorInflater.setupAnimatorForPath(AnimatorInflater.java:337)
        at android.animation.AnimatorInflater.parseAnimatorFromTypeArray(AnimatorInflater.java:283)
...
```
When the morphing involves only simple shapes is averagely simple to fix the paths by manually injecting or duplicating commands here and there; but when the complexity of the shapes grows, this task becomes quite tedious to do by hand (sometimes almost impossible). 
**VectAlign automagically aligns any pair of SVG paths (regardless of their complexity), creating a new pair of morphable paths without altering the original images.**


 Download
--
You can download the latest version of the pre-compiled jar from **[here]**.

 Changelog
--
 - **v0.2** 
  - added different morphing strategies (BASE, LINEAR, SUB\_BASE, SUB\_LINEAR)
  - added GUI (view, load and edit SVG/pathData, *morphing preview*)
  - added *export* function (automatically creates all the Android XML resources required)
 
 - **v0.1** 
  - first public release of VectAlign (command line only)

 Usage
--
Run VectAlign from command line by passing the two paths that you want to use in your morphing animation; you can pass these sequences by typing them directly or by referring a file (a simple txt file or even a standard SVG image); simply pass no params if you want to start the GUI:

**Examples of execution**

Run VectAlign in GUI mode:
```bash
java -jar  vectalign.jar 
```
```bash
java -jar  vectalign.jar --gui
```

Run VectAlign in command line mode:

```bash
java -jar  vectalign.jar   --start "M 10,20..."   --end "M 30,30..."
```
```bash
java -jar  vectalign.jar   --start image1.svg   --end image2.svg
```

The result represents the aligned (and *compatible*) version of the input paths/images: these new paths can be finally morphed using an `AnimatedVectorDrawable` without incurring in the *"Can't morph from X to Y"* exceptions:

**Example of output**
```bash
--------------------
  ALIGNMENT RESULT  
-------------------- 
# new START sequence:  
M 48.0,54.0 L 31.0,42.0 L 15.0,54.0 L 21.0,35.0 L 6.0,23.0 L 25.0,23.0 L 25.0,23.0 L 25.0,23.0 L 25.0,23.0 L 32.0,4.0 L 40.0,23.0 L 58.0,23.0 L 42.0,35.0 L 48.0,54.0 

# new END sequence:  
M 48.0,54.0 L 48.0,54.0 L 48.0,54.0 L 48.0,54.0 L 31.0,54.0 L 15.0,54.0 L 10.0,35.0 L 6.0,23.0 L 25.0,10.0 L 32.0,4.0 L 40.0,10.0 L 58.0,23.0 L 54.0,35.0 L 48.0,54.0 

```

**Available options**

```bash
usage: java -jar vectalign.jar  [-g] [-s <"string"|txt_file|svg_file>] [-e <"string"|txt_file|svg_file>] [-m <BASE|LINEAR|SUB_BASE|SUB_LINEAR>] [-v] [-h]

Align two VectorDrawable paths in order to allow morphing animations
between them.

 -g,--gui                                      Start VectAlign GUI
 -s,--start <"string"|txt_file|svg_file>       Starting VectorDrawable
                                               path ("string", txt file or
                                               SVG file)
 -e,--end <"string"|txt_file|svg_file>         Ending VectorDrawable path
                                               ("string", txt file or SVG
                                               file)
 -m,--mode <BASE|LINEAR|SUB_BASE|SUB_LINEAR>   Aligning technique (default
                                               is BASE)
 -v,--version                                  Print the version of the
                                               application
 -h,--help
```

How it works
--
VectAlign is based on an adaptation of the [Needleman-Wunsch] algorithm, which is used in bioinformatics to align protein or nucleotide sequences. 


Notes and known issues
--
 - This is an experimental tool which faces a complex task. **Result's quality may vary depending on the inputs**; thus, *wow effect* of the resulting animation is not guaranteed.
 - Aligning complex shapes **may create visual artifacts on one or both images**; in this case, try to simplify the original SVG path (e.g. using [InkScape]) and then run VectAlign again (see also the *Tips* section).
 - When referring a SVG file, all the path groups which compose the image will be merged in one single path.
 - If your SVG path is too much complex the system renderer will throw a silent exception: "*OpenGLRenderer﹕ Path too large to be rendered into a texture*"; in this case you need to simplify your image further.


Tips
--
 - When morphing complex aligned paths, for best result **avoid using the `fillColor` attribute  in your `VectorDrawable` and use the `strokeColor` only**. This because filled surfaces are more likely to be affected by artifacts than the stroke-only ones and usually provide less gorgeous morphing effects.
 - As general rule, *similar* images (in terms of SVG complexity) morph better than very different ones.
 - If you don't like the result of a morphing, try to alter the original images by simplifying the SVG path (e.g. using [InkScape]) and then run VectAlign again.
 - Since *AnimatedVectorDrawable* is API 21+ you can use the [vector-compat] library to extend support down to API 14+.

References
--
 - [DevBytes: Android Vector Graphics]
 - [AnimatedVectorDrawable] 
 - [VectorDrawable] 
 - [VectorDrawableCompat on support library v7 (partial)]
 - [SVG Paths Specification]
 

License
----

```bash
Copyright 2015, Stefano Bonetta.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
[AnimatedVectorDrawable]:http://developer.android.com/reference/android/graphics/drawable/AnimatedVectorDrawable.html
[VectorDrawable]:https://developer.android.com/reference/android/graphics/drawable/VectorDrawable.html
[DevBytes: Android Vector Graphics]:https://www.youtube.com/watch?v=wlFVIIstKmA
[InkScape]:https://inkscape.org/en/
[vector-compat]:https://github.com/wnafee/vector-compat
[Needleman-Wunsch]:https://en.wikipedia.org/wiki/Needleman%E2%80%93Wunsch_algorithm
[official docs]:https://developer.android.com/reference/android/graphics/drawable/AnimatedVectorDrawable.html
[SVG Paths Specification]:http://www.w3.org/TR/SVG/paths.html
[VectorDrawableCompat on support library v7 (partial)]:https://android.googlesource.com/platform/frameworks/support/+/master/v7/vectordrawable/src/android/support/v7/graphics/drawable
[sample project]:https://github.com/bonnyfone/vectalign-samples/
[here]:https://bintray.com/artifact/download/bonnyfone/maven/vectalign-0.2-jar-with-dependencies.jar
