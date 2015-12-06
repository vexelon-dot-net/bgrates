BGrates HISTORY file
=====================

## v3.0.1

  * Fixed crash when updating over 4G connections.

## v3.0.0

### EN

  * Redesigned the app user interface.
  * Automatic daily currency updates.
  * Convert inbetween currencies based on Bulgarian Lev.
  * Added Euro to the list of currencies.
  * Minor updates and fixes.
  
### BG

  * Редизайн на интерфейса на приложението.
  * Автоматично обновяване на валутите ежедневно.
  * Конвертиране на чужда валута към друга такава базирано на българският лев.
  * Добавяне на Евро към валути.
  * Други подобрения и оптимизации.
  

## v2.3.0

  * Updated icons and images. 
  * Minor fixes in texts.
  * Fixed an issue where old currency rate date was not being displayed.

## v2.2.0

  * Added EUR currency rate information. (Issue #1)
  * Requirements downgraded to devices with firmware 1.5.  
  * "Update Rates" button no longer available, since it repeats functionality of already available (EN) and (BG) update buttons.
  * The app can now be moved to SDCard storage. This is the default install location as well.

## v2.1.0

  * Fixed division by zero bug when converting from currencies with 0 exchange rate (ISK case).
  * Fixed bug that caused tendency flag to be shown after update for currencies without a change in the rate.
  * Added flags and styling of spinner when using the Convert activity.
  * Clicking on currency now opens a dedicated view for currency rate comparison before and after update. 
  
## v2.0.0

  * Texts on about screen and also fixed the scrolling of the Activity for devices with smaller screen resolutions.
  * Font size and colors on main activity screen. Precision numbers for each currency now shown with gray text.  
  * The Hint/Toast window that is shown when clicking on a currency from the list now shows old and new currency values if available.
  * Added currency increase/decrease rate status. It is now possible to view currency tendencies when updating to the latest exchange rates. The tendencies are marked with green arrow for "UP" and red arrow for "DOWN". (Issue #3)
  * Added currency convert feature. It is now possible to convert between currencies using the Convert activity from the menu. (Issue #2)

## v1.1.0

  * Fixed Error during startup - "The application BGrates MTel ... has stopped unexpectedly". (Issue #1)
  * Added web site of application in About screen.
