_.o0 BGRates 0o._ 
=========================

[![Build Status](https://travis-ci.org/petarov/bgrates.svg?branch=dev)](https://travis-ci.org/petarov/bgrates) 

<a href="https://play.google.com/store/apps/details?id=net.vexelon.bgrates&utm_source=global_co&utm_medium=prtnr&utm_content=Mar2515&utm_campaign=PartBadge&pcampaignid=MKT-AC-global-none-all-co-pr-py-PartBadges-Oct1515-1">
<img alt="Get it on Google Play" src="https://play.google.com/intl/en_us/badges/images/apps/en-play-badge.png" width="180px" />
</a>

[BGrates](https://play.google.com/store/apps/details?id=net.vexelon.bgrates) is an Android application that delivers up-to-date currency exchange rates for Bulgarian Lev (BGN). Exchange rate information is updated from the web site of the Bulgarian National Bank - http://www.bnb.bg

The exchange rates of currencies are calculated on the basis of the fixed exchange rate of the Bulgarian Lev to the Euro, and the information source is the exchange rates of the Euro against these currencies published by the European Central Bank

Please read [HISTORY](HISTORY) for a list of changes.

# Requirements

  * Requires Android 4.0.3 (API Level 15+)
  * Requires Internet connection
  * Supported Localization - EN (default), bg_BG

# Development 

[![Join the chat at https://gitter.im/petarov/bgrates](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/petarov/bgrates?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)


To generate Eclipse project files run:

    gradle eclipse
    
To build the project run:

    gradle clean build

To install a debug build run:

    gradle installDebug

To check what other tasks are available run:

    gradle tasks

# License
[MIT License](LICENSE)
