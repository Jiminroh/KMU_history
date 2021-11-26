import socket
import select
from urllib.parse import urlparse

print("Student ID : 20171617")
print("Name : Jimin Roh\r\n")

while True:
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    in_url = input(">")
    if in_url == "quit":
        break
    if in_url[0:3] == "get":
        url = urlparse(in_url[4:])
        if url.scheme != "http":
            print("Only support http, not https")
            pass
        else:
            print(url)
            print("GET "+url.path+" HTTP/1.0")
            print("Host: "+ url.hostname)
            print("User HW1/1.0")
            print("Connection: close\r\n")
            try:
                s.connect((url.hostname, 80))

                msg = "GET "+url.path+" HTTP/1.0\r\n\r\n"
                s.sendall(msg.encode())

                reply = b''

                sum = 0
                cnt=10

                i = 1
                while select.select([s], [], [], 3)[0]:
                    data = s.recv(2048)
                    if not data: break
                    reply += data
                    sum += len(data)

                    headers = reply.split(b'\r\n\r\n')[0]
                    t = headers.split(b'\r\n')
                    t1 = t[0].split()[1]
                    if t1 != b'200':
                        print("404 Not Found")
                        break
                    size = -1
                    for line in t:
                        if b'Content-Length:' in line:
                            size = int(line.split()[1])
                            break

                    if i == 1:
                        i = 0
                        print("Total Size ", size, " bytes")

                    if sum > size:
                        print("Current Downloading ", size, "/", size, " (bytes) ", round(sum / size * 100), "%")
                        print("Download Complete: ", p, ", ", size, "/", size)
                    elif round(sum/size*100) >= cnt:
                        cnt += 10
                        print("Current Downloading ", sum,"/",size," (bytes) ", round(sum/size*100), "%")

                    image = reply[len(headers)+4:]

                    p = url.path.split("/")[-1]

                    f = open(p, 'wb')
                    f.write(image)
                    f.close()

            except:
                print("cannot connect to server")
                pass
