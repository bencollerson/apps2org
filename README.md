
Apps2Org fork by willemw12
==========================

Apps2Org in a newer user interface (UI).

![screenshot](https://github.com/willemw12/apps2org/wiki/images/Screenshot_01.png) ![screenshot](https://github.com/willemw12/apps2org/wiki/images/Screenshot_02.png) ![screenshot](https://github.com/willemw12/apps2org/wiki/images/Screenshot_03.png)

![screenshot](https://github.com/willemw12/apps2org/wiki/images/Screenshot_04.png) ![screenshot](https://github.com/willemw12/apps2org/wiki/images/Screenshot_05.png) ![screenshot](https://github.com/willemw12/apps2org/wiki/images/Screenshot_06.png)

![screenshot](https://github.com/willemw12/apps2org/wiki/images/Screenshot_07.png) ![screenshot](https://github.com/willemw12/apps2org/wiki/images/Screenshot_08.png) ![screenshot](https://github.com/willemw12/apps2org/wiki/images/Screenshot_09.png)

Requirements: Android 6.0 (SDK 23) or higher.

Get the APK from the [release](https://github.com/willemw12/apps2org/releases) page.

Known issues:

* The Apps and Labels lists are not immediately updated (caused by removal of some deprecated code).
* Files exported to Google Drive are empty.

The changes of this fork are on the "ui" git branch (git checkout ui).

This program is licensed under GPLv3 (see included COPYING file).

Material Design icons by Google http://google.github.io/material-design-icons/.


Apps2Org
========

Apps2Org is an Android application categorizer/launcher. It is a fork
of (apparently no longer being maintained) Apps Organizer
https://code.google.com/p/appsorganizer .

The main feature of Apps2Org is the ability to organize installed
application by multiple categories/tags/labels. Once that it is done,
it allows to browse thru the categories and launch applications. Unlike
other application launchers, Apps2Org doesn't replace standard
home screen/launcher application, but instead provides its "launching"
functionality via set of widgets/shortcuts which you can put in home
screen using an existing launcher. This allows you to keep a home
screen application of your choice (or to change it easily), while still
having access to sophisticated categorization capabilities provided
by Apps2Org.

You can get more info about functionality inherited from Apps Organizer
on its homepage, https://code.google.com/p/appsorganizer . Following
changes were applied to the Apps2Org fork:

- For "All Labels" widget/shortcut, show also "Other Apps" category.
This is last step required to truly use Apps2Org as full-featured
application launcher - after all, newly installed applications are not
yet categorized, so without this feature, it is additional chore to
access them.

- It's possible to star an application from widget, using Choose Labels
context menu. Furthermore, it's possible to star/unstar an app directly
from widget context menu. These feature make it very easy to maintain
your categories and favorites "on the go".

- Widget application grid no longer closes by tapping on window title.
This oftentimes happens while "on the run" or with one-handed operation
when trying to click Starred Apps icon located in the title. So, now one
mis-tap won't make you retry applicaton selection sequence from scratch.
At the same time, tapping well outside the widget window will still close
it, as you expect.

- Other minor features and fixes - see git changelog.

Recommended way to use Apps2Org is with existing home screen/launcher.
If you are looking for good OpenSource launcher, ADW-Launcher
(https://github.com/AnderWeb/android_packages_apps_Launcher,
https://f-droid.org/repository/browse/?fdid=org.adw.launcher) is
recommended. With it and similar launchers, you can replace its
"App Drawer" action widget in floating part of a screen (one which
stays available on each screen) with Apps2Org "All Labels" shortcut.
Of course, you can put it as a normal home screen shortcut either.

Note: Apps2Org provides both "widget" and "shortcut". ADW supports
only shortcuts in "App Drawer".

Quick setup instructions:

1. Once Apps2Org installed, go to home screen.
2. Tap and hold on free spot, in the opening menu select "Shortcuts".
3. Select "Apps2Org".
4. Select "All labels".
5. (Optionally, for ADW-Launcher). Drag away standard Launcher widget
from "App Drawer" at the bottom of screen.
6. Drag Apps2Org "All labels" shortuct to just freed space on App
Drawer.
7. Categorize your apps!

If you look for easy way to install Apps2Org, ADW-Launcher, and other
OpenSource Android software, try http://f-droid.org .
