From [this Tutorial](https://learn.microsoft.com/en-us/azure/azure-functions/functions-event-grid-blob-trigger?tabs=isolated-process%2Cnodejs-v4&pivots=programming-language-csharp#start-local-debugging)

1. Local vs code azure function dev : [Further Details](https://learn.microsoft.com/en-us/azure/azure-functions/functions-develop-vs-code?tabs=csharp#run-functions-locally) 
1. [Install azurite](https://learn.microsoft.com/en-us/azure/storage/common/storage-use-azurite?tabs=visual-studio) 
1. Start azurite : 
	```
    In Visual Studio Code : Ctrl + P > Azurite : Start 
    ```
1. Configure Azure Function to use local development storage : 
	``` json
	{
  		"Values": {
    		"AzureWebJobsStorage": "UseDevelopmentStorage=true",
    		...
  		}
    }
    ```
1. Install NGrok to tunnel the connection from internet : 
	- [Download Ngrok](https://ngrok.com/download)
	- [Sign Up for an oAuth token](https://dashboard.ngrok.com/signup)
1. Set the token in the config :  
	``` bash
	ngrok config add-authtoken <token>
	```
1. Create a custom endpoint (free and optional : Will help with keeping the same domain name for future tests) : 
	- [Create a free endpoint](https://dashboard.ngrok.com/cloud-edge/endpoints)
1. Create an edge tunnel and configure it to the azure function host :
	``` bash
    ngrok config edit
    ```
    - Inside the file (retrieve the config from `Cloud Edge > Edges > Start a Tunnel > Start a tunnel from a config file`: 
    ``` yaml
    version: 2
	authtoken: xxx
	tunnels:
  	  my_tunnel_name:
    	labels:
          - edge=xxx
        addr: http://localhost:80
    ```
1. Manually serve the tunnel from the bash terminal : 
	``` bash
	ngrok tunnel --label edge=edghts_2ShvSEl8k4SQImfDoMaECki0yop http://localhost:7071
	```
1. Configure the task.json (Java Sample) to expose the azure function to the internet : 
	``` json
	{
      "type":"shell",
      "label" : "Expose debug to internet",
      "command": "ngrok tunnel --label edge=edghts_2ShvSEl8k4SQImfDoMaECki0yop http://localhost:7071",
      "dependsOn": "func: host start"
    },
    {
      "type": "func",
      "label": "func: host start",
      "command": "host start",
      "problemMatcher": "$func-java-watch",
      "isBackground": true,
      "options": {
        "cwd": "${workspaceFolder}/target/azure-functions/unzipfunc-20230717151702055"
      },
      "dependsOn": "package (functions)"
    },
	```
1. Configure the launch.json to offer another debug option (calling the task.json): 
	``` json
    {
      "name": "Expose to Internet",
      "type": "java",
      "request": "attach",
      "hostName": "127.0.0.1",
      "port": 5005,
      "preLaunchTask": "Expose debug to internet"
    }
    ```
1. Now the Azure function is running and exposed via the ngrok proxy, it's time to wire up Event Grid :
	1. Create an Event Grid System Topic Subscription from an `Azure Storage Account` `Event tab` for example
    1. Define the endpoint to [HTTP Webhook as follows](https://learn.microsoft.com/en-us/azure/azure-functions/functions-event-grid-blob-trigger?pivots=programming-language-java#build-the-endpoint-url) : 
    ``` bash
    https://<ngrok-endpoint>/runtime/webhooks/eventgrid?functionName=<FunctionName>
    ```
1. In my example : 
    ```bash
    https://carefully-awake-jennet.ngrok-free.app/runtime/webhooks/eventgrid?functionName=EventGridExample
    ```
1. Then start everything together from vscode `debug > Expose to Internet`: 
   1. Bare in mind that the reverse-proxy will be exposed via the token configured in the `ngrok config edit` step and communication remains encrypted through https

The source code can be found here : https://github.com/Justrebl/AzureFunction-unzipbigfiles 
