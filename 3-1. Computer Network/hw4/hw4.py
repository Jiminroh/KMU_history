from socket import *
from select import *
import sys

HOST = ''
PORT = int(sys.argv[1])
BUFSIZE = 1024
ADDR = (HOST, PORT)
ID = sys.argv[2]

serverSocket = socket(AF_INET, SOCK_STREAM)
clientSocket = socket(AF_INET, SOCK_STREAM)

serverSocket.bind(ADDR)

serverSocket.listen(10)
connection_list = [sys.stdin, serverSocket]

print("Student ID : 20171617")
print("Name : Jimin Roh")
print(ID+">")


def prompt():
    sys.stdout.write(ID +'>\n')
    sys.stdout.flush()

def client_socket(s):
    try:
        clientSocket.connect((s[1], int(s[2])))
    except Exception as e:
        sys.exit()

    while True:
        try:
            connection_list = [sys.stdin, clientSocket]

            read_socket, write_socket, error_socket = select(connection_list, [], [], 1)

            for sock in read_socket:
                if sock == clientSocket:
                    data = sock.recv(BUFSIZE)
                    if not data:
                        clientSocket.close()
                        sys.exit()
                    else:
                        prompt()
                        print('%s' % data.decode())
                else:
                    message = sys.stdin.readline()
                    if message[0:5] == '@quit':
                        clientSocket.close()
                        sys.exit()
                    clientSocket.send((ID + " : " + message).encode())
                    prompt()
        except KeyboardInterrupt:
            clientSocket.close()
            sys.exit()


connecting=0
while True:

    read_socket, write_socket, error_socket = select(connection_list, [], [], 1)
    for sock in read_socket:

        if sock == serverSocket:
            clientSocket, addr_info = serverSocket.accept()
            connection_list.append(clientSocket)
            fd = str(clientSocket).find('fd=')+3
            connecting = 1
            print("connection from host {0}, port {1}, socket {2}".format(addr_info[0], addr_info[1], str(clientSocket)[fd]))

        elif sock == sys.stdin:
            prompt()
            msg = sys.stdin.readline()
            if msg == '@quit\n':
                break
            elif msg[0:5] == '@talk':
                s = msg.split(" ")
                client_socket(s)
            elif connecting == 1:
                clientSocket.send((ID + " : " + msg).encode())
            else:
                break


        else:
            data = sock.recv(BUFSIZE)
            if data:
                print(data.decode())
            else:
                connecting = 0
                fd = str(sock).find('fd=') + 3
                print('Connection Closed {0}'.format(str(sock)[fd]))
                connection_list.remove(sock)
                sock.close()






