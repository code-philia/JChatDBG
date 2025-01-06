import os
import sys
import time
from openai import OpenAI

client = OpenAI()
while True:
    try:
        user_input = sys.stdin.readline().strip()
        if not user_input:
            print("No input received. Waiting for input...")
            time.sleep(2)
            continue

        completion = client.chat.completions.create(
            model="gpt-4o-2024-05-13",
            messages=[
                {"role": "system", "content": "You are a helpful assistant."},
                {"role": "user", "content": user_input}
            ]
        )
        if getattr(completion.choices[0].message, 'content', None):
            content = completion.choices[0].message.content
            # print(completion)
            # print('\n')
            print(content)
            break
        else:
            print('error_wait_2s')
    except:
        pass
    time.sleep(2)