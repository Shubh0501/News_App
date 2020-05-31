# News_App

This is an Android App written in **Kotlin language**. The app uses the API of https://newsapi.org/ to get the latest news information and shows it to the user.

### Some basic points which should be ensured while using the app for now are :
1. In case Internet service is not available on the phone and the app is opened for the first time, no news data will be shown. If the user connects to the internet while using the app, make sure to pull down to refresh the data.
2. News data will be saved in the internal memory of the app for future use i.e. when the user uses the app in future without internet connection.
3. Every time the app gets active internet connection, the data downloaded will be overwritten with the new data available.
4. Some Images might be a little compressed or expanded as the exact dimension of all the images vary a lot.
5. Find the apk of the app at : [/Apk/NewsApp.apk](https://github.com/Shubh0501/News_App/blob/master/Apk/NewsApp.apk)

## If you want to run the code and see the working of the app in your emulator or device, You will need an API key which can be found at : https://newsapi.org/ 

### After getting an API key from the mentioned URL,kindly follow these steps:
1. On your platform, navigate to 'app/src/main/res/values folder
2. You can either create a new xml file here or navigate to '/strings.xml'
3. Create a new string resource by the name of **news_api_key** and set your API key as the value of this resource.

You are good to go and use the app without any problems hopefully :sweat_smile:

## Enjoy!
