import socket
import sys
from os.path import exists

print("Student ID : 20171617")
print("Name : Jimin Roh\r\n")

while True:
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.bind(('localhost', int(sys.argv[1])))
    s.listen(7)

    c, addr = s.accept()
    print("Connection : Host IP ", addr[0], "Port ", addr[1], "socket 4")

    # http request 메세지 받기
    data = c.recv(1024)
    request_data = data.decode().split("\r\n")
    for i in range(len(request_data)):
        print(request_data[i])

    # http reponse 메세지 보내기
    for chunk in request_data:
        if "GET" in chunk:
            request_method = chunk.split(" ")[0]
            request_version = chunk.split(" ")[2]
            filename = chunk.split("/")[1].split(" ")[0]

    data_transferred = 0
    if not exists(filename):
        print("Server Error : No such file ", filename)
        header = "{0} 404 NOT FOUND\nConnection: close\nContent-Length: 0\nContent-Type: text/html".format(request_version)
    else:
        with open(filename, 'rb') as f:
            try:
                while data: #데이터가 없을 때까지
                    data = f.read(60240)  # 1024바이트 읽는다
                    data_transferred += len(data) #1024바이트 보내고 크기 저장
            except Exception as ex:
                print(ex)
        header = "{0} 200 OK\nConnection: close\nContent-Length: {1}\nContent-Type: text/html".format(request_version, data_transferred)

    response_data = header;

    response_data += "\r\n\r\n"

    if (response_data.split("\n")[0] == request_version," 200 OK"):
        with open(filename, 'rb') as f:
            try:
                data = f.read(60240)  # 1024바이트 읽는다
                while data:  # 데이터가 없을 때까지
                    response_data += data.decode()  # 1024바이트 보내고 크기 저장
                    data = f.read(60240)  # 1024바이트 읽음
            except Exception as ex:
                print(ex)

    c.send(response_data.encode())






