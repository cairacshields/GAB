# GAB

As you've probably guessed, GAB is an awesome (emphasis on AWESOME), Android chat app that could use some help.

*NOTE: If you are forking the code, please be sure to run the application on an actual device. Using an emulator will not work due to the resctriction on Google Play. You'll also need to sign in using your existing social media platform account.

What does this project do?

       ~ GAB is an Android chat app that connects users from across the world. The platform is meant to be an open space to 
         engage and interact with those that have similar interest. 
    
      ~ GAB is still in development, there are a few minor tweaks and final snippets of code that need to be written. 
        I'm hoping that some awesome coder can take a moment and contribute. 
   
      ~ GAB takes advantage of Facebook and Google Plus login and authentication protocols. It also makes use of the Giphy 
        API, for searching and pulling the requested GIF's.
  
      ~ GAB uses FireBase as it's realtime database, authentication manager, and storage system.
		
      ~ NOT a dating, flirting, or random hook up app. (There are plenty of those!)
  
     ~ If Tinder, Twitter, and Instagram are the jocks and popular kids... Gab is the nerd that grows up to be a     billionaire! 


Why is this project useful?

      ~ GAB was developed to link introverts with their long lost kin. (You know, the ones that share that same heart throbbing
        love for Star Trek.) For some reason, all the awesome people don't seem to live nearby, I wonder if they even 
        exist? That's why GAB was created, it's the place where anyone can go to find people with similar interests. It is NOT 
        a dating app, nor is it a flirting app! Please don't try to make it one. 

      ~ GAB has a very 'Craigslist' style approach. You can either search for an existing topic, or you can create a new 'post'
        about... anything! If someone wants to talk about your topic, they simply join the thread. You can save your favorite 
        threads, send GIF's and add people as your hommies (Okay, the last part needs some major tinkering!). 
        How do I get started?

      ~ I want GAB to prosper. I want it to be the place where people of all backgrounds feel comfortable speaking their mind 
        safely. 
      ~ That being said, GAB is open sourced so that anyone who feels like this platform could flourish, can add changes and 
        some of the much needed finalizations. I WANT people to help in getting this project finished. I started it alone and 
        it's not a simple task. 


What needs to be done?

    ~ Let me start by explaining what is finished as of now. 
   
       - User authentication and management. (i.e. password resest, account deletion, account settings.) 
       - Basic UI appearance and theme has already been coded up. 
       - Handling new thread creations. (Sending new post and post details to the appropriate database location, 
         using that information to populate and update the realtime list of threads.) 
       - Seperating and managing user messages according to each individual thread. 
    
   ~ So, what needs to be done?
    
      - I've already written some of the code for a user's 'favorite' threads, this needs tweaking. My idea is to use shared preferences to  
        store a user's favorite threads. Notifications will be sent to the user by Firebase's notifications system. 
      - The Giphy API is already connected and is set to load the most popular GIF's of the day once a user click's on the 
        'GIF' icon (Fork the code to run it to view the UI). However, the code for parsing the data recieved and sending
        the GIF's embeded URL to the database still needs to be written. 
      - There are a number of other small tweaks that need attention such as user profile tab and the overall Material
        Design layout of the app. 
      - I greatly appreciate all contributions and look forward to having GAB on the marketplace soon!


Where can I get more help, if I need it?

    ~ Feel free to shoot me an email if you want to work on this project or if you have any questions about GAB. My email's on my profile, but just in case your 
    fingers are feeling lazy, here it is: cairacshields@gmail.com 
