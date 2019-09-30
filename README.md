# amp-t2d-property-registry
Registry service for AMP T2D properties.
 
 ### OAuth2 authenticated rest content server
 
 The authentication is provided by google api.
 
 To perform any valid authenticated request please follow the below steps,
 
 #### What you need are the following:
 
 * Client ID - 548139723323-g70r2bllnlkcgcq85vo5kjshkegitomk.apps.googleusercontent.com
 * Client Secret - uYtbvxGubqz5hQGAgGH2P0Qm
 
 #### Requesting Authorization
   Now replace the values needed in the following link and put it in a web browser.
  
  ``` https://accounts.google.com/o/oauth2/auth?client_id=[Application Client Id]&redirect_uri=urn:ietf:wg:oauth:2.0:oob&scope=[Scopes]&response_type=code ```
  
  
 #### Exchanging Authentication code
   You should get the standard request for authentication.   Once you have accepted authentication copy the Authentication code.   Take the following code replace the values as needed.
 
 ```
 curl --request POST --data "code=[Authentication code from authorization link]&client_id=[Application Client 
 Id]&client_secret=[Application Client Secret]&redirect_uri=urn:ietf:wg:oauth:2.0:oob&grant_type=authorization_code"  https://accounts.google.com/o/oauth2/token  
 ```
 
 You should get something like this:
 
 ``` {  
 “access_token”:“XXXXX”,
 “expires_in”:3600,
 “id_token”:“XXXXX”,
 “refresh_token”:“XXXXX”,
 “token_type”:“Bearer”
 } 
 ``` 
 
 You now have an access token you can use in your Google API call.
 
 #### Accessing the endpoints
   To access one of the secure rest services we send the access_token like this
 
 ```
 curl -H "Authorization: Bearer 27647b94-1f9f-4945-ae8f-6521d48fdcad" <host>:<port>/properties/
 ```
 
### Sending tokens in swagger
 Please pass the token in Authorize box value as "Bearer \<Token>"
