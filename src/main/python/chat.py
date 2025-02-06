import socket
import time
import json
from openai import OpenAI

class ChatServer:
    def __init__(self):
        self.client = OpenAI()
        self.tools = self.get_tools()
        self.conversation = [{'role': 'system', 'content': 'You are a helpful assistant.'}]
        self.responses = ""
        
        self.server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.server_address = ('localhost', 6789)
        self.server_socket.bind(self.server_address)
        self.server_socket.listen(1)
        self.connection = None
        
    def read_line(self):
        data = b""
        while True:
            part = self.connection.recv(1)
            data += part
            if part == b"\n":
                break
        return data.decode('GBK')
        
    def read_message(self):
        data = ""
        while True:
            line = self.read_line()
            if line == "END\n":
                break
            data += line
        if data.endswith("\n"):
            data = data[:-1]
        return data
        
    def send_message(self, message):
        encoded_break = '\n'.encode('GBK')
        encoded_message = message.encode('GBK')
        self.connection.sendall(encoded_message + encoded_break)
        self.connection.sendall("END\n".encode('GBK'))  # This is a signal to end the message
        
    def get_response(self, message):
        self.send_message(message)
        # response = self.connection.recv(1024)
        response = self.read_message()
        return response
        
    def get_tools(self):
        function_list = ["info", "debug"]
        tools = []
        for function_name in function_list:
            function = getattr(self, function_name)
            schema = json.loads(function.__doc__)
            tools.append(schema)
        return tools
    
    def add_function_results_to_conversation(self, completion):
        tool_calls = completion.choices[0].message.tool_calls
        for tool_call in tool_calls:
            function_name = tool_call.function.name
            print(f"Invoke function: {function_name}")
            function_arguments = tool_call.function.arguments
            function_arguments = json.loads(function_arguments)
            print(f"Function arguments: {function_arguments}")
            function_return_value = getattr(self, function_name)(**function_arguments)
            print(f"Function return value: {function_return_value}")
            response = {
                "tool_call_id": tool_call.id,
                "role": "tool",
                "name": function_name,
                "content": function_return_value
            }
            self.conversation.append({"role": "assistant", "tool_calls": tool_calls})   # role为tool的消息必须是对前面assistant消息中tool_calls的响应
            self.conversation.append(response)
            
    def keep_asking(self):
        while True:
            completion = self.client.chat.completions.create(
                model="gpt-4o-2024-05-13",
                messages=self.conversation,
                tools=self.tools
            )
            print('Get completion')
            response_message = completion.choices[0].message
            if response_message.content:
                print(f"GPT response: {response_message.content}")
                self.responses += response_message.content
                self.responses += "\n"
            if completion.choices[0].finish_reason == "tool_calls":
                print("Get tool calls")
                self.add_function_results_to_conversation(completion)
            else:
                break

    def run(self):
        self.connection, _ = self.server_socket.accept()
        try:
            # question = self.connection.recv(1024)
            question = self.read_message()
            print(f"Get question: {question}")
            self.conversation.append({'role': 'user', 'content': question})
            self.keep_asking()
            self.send_message(self.responses)
        finally:
            print("Connection closed")
            
    ### Callbacks for LLM
    
    def info(self, class_name, method_name):
        """
        {
            "type": "function",
            "function": {
                "name": "info",
                "description": "Call the `info` function to get the documentation and source code for a function. Unless it is from a common, widely-used library, you MUST call `info` exactly once on any symbol that is referenced in code leading up to the error.",
                "parameters": {
                    "type": "object",
                    "properties": {
                        "class_name": {
                            "type": "string",
                            "description": "The name of the class that contains the function.",
                            "example": "org.apache.commons.math3.optimization.direct.CMAESOptimizerTest"
                        },
                        "method_name": {
                            "type": "string",
                            "description": "The name of the function to get the documentation and source code for.",
                            "example": "testFitAccuracyDependsOnBoundary"
                        }
                    },
                    "required": ["class_name", "method_name"]
                }
            }
        }
        """
        return self.get_response(f"info {class_name} {method_name}")
    
    def debug(self, command):
        """
        {
            "type": "function",
            "function": {
                "name": "debug",
                "description": "Call the `debug` function to run JDB debugger commands on the stopped program. You may call the `debug` function to run the following commands: `where`, `up`, `down`, `print`, `list`.  Call `debug` to print any variable value or expression that you believe may contribute to the error.",
                "parameters": {
                    "type": "object",
                    "properties": {
                        "command": {
                            "type": "string",
                            "description": "The JDB command to run."
                        }
                    },
                    "required": [ "command" ]
                }
            }
        }
        """
        return self.get_response(f"debug {command}")

if __name__ == "__main__":
    chat_server = ChatServer()
    chat_server.run()