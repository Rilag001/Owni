# Owni

Owni is a Android app that keeps track of your personal debts. With Owni you always know who you owe 14$, the book The Catcher in the Rye and a hug, or who owes you 13.5 Euro, one Lionking DVD and 5 beers. Never again will you forget who borrowed Beverly Hills Cop II, Owni gives you full control.

## App definition statement

A Owni always pays it´s debts. Keeping track of you debts.

## Design and functionality

The goal with Owni is to give the user a glancable overview over your debts on all your Android phones and tablets. Owni uses Firebase as a back-end to persist data and provide simple login.<br><br>
<img src="https://raw.githubusercontent.com/Rilag001/Owni/master/owni-login.png" alt="alt text" width="420px">
<img src="https://raw.githubusercontent.com/Rilag001/Owni/master/owni-create-account.png" alt="alt text" width="420px">

Ones logged in the user selects a currency, that will be used for later calculations. Debts are organized with cards in two ways, personal debts and activities debts. Each card gives a overview and shows name, amount of items, and balance in your selected currency (both with text and color: red for you owe more, green for other people owe you more and blue for you are squared).  <br><br>
<img src="https://raw.githubusercontent.com/Rilag001/Owni/master/owni-select-currency.png" alt="alt text" width="420px">
<img src="https://raw.githubusercontent.com/Rilag001/Owni/master/owni-peoplefragment.png" alt="alt text" width="420px">

When you click on a card you open a second activity to view its items. Here you can create and delet items, as well as the parant card. Owni automatically calculates the debt balance in your selected currency. When you expand the Floating actionbutton menu you also get the possibilty to open Swish.<br><br>
<img src="https://raw.githubusercontent.com/Rilag001/Owni/master/owni-peopleitem.png" alt="alt text" width="420px">
<img src="https://raw.githubusercontent.com/Rilag001/Owni/master/owni-activitiesfragment-dialog.png" alt="alt text" width="420px">

## Requirements
Owni works with Android 4.1 and above. It requirements a internet connection.  

## Known bugs
No one yet. If you find one, please feel free to fork this project. 

## Credits/acknowledgements
<ul>
  <li>Owni uses Firebase for backend and login cabability, and FirebaseUI for view binding in app: https://www.firebase.com/</li>
  <li>It uses a background image from the awsome site Unsplash: from: https://unsplash.com/photos/8mqOw4DBBSg</li>
  <li>Shout-out to Dedaniya HirenKumar for tip about using listview inside a scrollview: http://stackoverflow.com/questions/18813296/non-scrollable-listview-inside-scrollview</li>
  <li>Uses awsome Fab animation from blackcj: http://stackoverflow.com/questions/31415742/how-to-change-floatingactionbutton-between-tabs/31418573</li>
  <li>Uses awsome FabMenu library created by chalup: https://github.com/futuresimple/android-floating-action-button</li>
</ul>
And finally a big thank you to my teacher Oskar Björnman and my fellow student at Nackademin (Stockholm, Sweden) for your invaluable feed-back during this project.
