import socket
import time
import json
from openai import OpenAI

class ChatServer:
    def __init__(self):
        self.client = OpenAI()
        self.tools = self.get_tools()
        self.conversation = [{'role': 'system', 'content': 'You are a helpful assistant.'}]
        
        self.server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.server_address = ('localhost', 6789)
        self.server_socket.bind(self.server_address)
        self.server_socket.listen(1)
        
    def get_weather(self, location):
        return f"The weather in {location} is sunny."
        
    def get_tools(self):
        # TODO: Later use a seperate file `functions.py` to store all the functions
        tools = [
            {
                "type": "function",
                "function": {
                    "name": "get_weather",
                    "parameters": {
                        "type": "object",
                        "properties": {
                            "location": {"type": "string"}
                        }
                    }
                }
            }
        ]
        return tools
    
    def add_function_results_to_conversation(self, completion):
        tool_calls = completion.choices[0].message.tool_calls
        for tool_call in tool_calls:
            function_name = tool_call.function.name
            function_arguments = tool_call.function.arguments
            function_arguments = json.loads(function_arguments)
            function_return_value = getattr(self, function_name)(**function_arguments)
            response = {
                "tool_call_id": tool_call.id,
                "role": "tool",
                "name": function_name,
                "content": function_return_value
            }
            self.conversation.append({"role": "assistant", "tool_calls": tool_calls})   # role为tool的消息必须是对前面assistant消息中tool_calls的响应
            self.conversation.append(response)

    def get_openai_response(self, message):
        while True:
            try:
                completion = self.client.chat.completions.create(
                    model="gpt-4o-2024-05-13",
                    messages=[
                        {"role": "system", "content": "You are a helpful assistant."},
                        {"role": "user", "content": message}
                    ]
                )
                if getattr(completion.choices[0].message, "content", None):
                    return completion.choices[0].message.content
                else:
                    time.sleep(2)
            except:
                pass

    def run(self):
        connection, _ = self.server_socket.accept()
        try:
            data = connection.recv(1024)
            response = self.get_openai_response(data.decode('utf-8'))
            connection.sendall(response.encode('utf-8'))
        finally:
            connection.close()
            print("Connection closed")

if __name__ == "__main__":
    chat_server = ChatServer()
    chat_server.run()