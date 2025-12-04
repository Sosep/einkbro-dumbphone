# EinkBro-Dumbphone
Fork of [EinkBro](https://github.com/plateaukao/einkbro) with some modifications thinking on being used for external apps to open links
and log ins.

I DON'T know a lot of android app development so what a was looking for doing this is that, with the
modifications, will continue working. Probably the changes could be better, so if you want to,
create a PR or fork this or the original repo. I am only a person who wants to try to quit from phone
addiction in a world which relies on phones for too many things, without alternatives :)

The idea is to uninstall all the browsers and apps stores on a android devices using adb, only leaving
the apps that we are going to use daily.

The problem. Many apps relies on browser to open link and, more important, login, so one
is necessary. I tried to make a version of EinkBro which solves this problem, but due to the drawback
of allowing opening links, use some resources to make the navigation experience a little bit harder.

I think the best approach is to save the apk only to install in case some app needs it

### Modifications:
- In order to not to allow open new tabs or search
  - Most of the buttons of the toolbar have been hidden
  - Most of the options shown when we select a text, link, resource have been hidden

- The access to Settings has been deleted to disallow user to disable the next options. This is for
the first's version until i clean the setting options

- Setting Options setted as default
  - The option `Should Save Tabs` has been disable to close all the tabs after exiting the browser
  - The next options are enabled to delete all data after close the browser
    - `Clear Cache`
    - `Clear History`
    - `Clear "Indexed Databases" and "Local WebView Storage"`
    - `Clear Cookies`
    - `Clear on app exit`

- The default home url has been set as `https://altl.io/` instead of `https://www.google.com/`
