from socket import *
from select import *
import sys

print("Student ID : 20171617")
print("Name : Jimin Roh")

s = socket(AF_INET, SOCK_STREAM)
s.bind(('localhost', int(sys.argv[1])))
s.listen(8)
c_list = [s]
fd = []

while c_list:
    try:
        r_socket, w_socket, e_socket = select(c_list, [], [])

        for sock in r_socket:
            if sock == s:
                clientSocket, addr = s.accept()
                c_list.append(clientSocket)
                fd.append(clientSocket)
                print("connection from host {0}, port {1}, socket {2}".format(addr[0], addr[1], fd.index(clientSocket)+3))

                for socket_in_list in c_list:
                    if socket_in_list != s and socket_in_list != sock:
                        try:
                            msg = "\n"
                            socket_in_list.send(msg.encode())
                        except Exception as e:
                            socket_in_list.close()
                            c_list.remove(socket_in_list)
            else:
                data = sock.recv(1024)
                if data:
                    for socket_in_list in c_list:
                        if socket_in_list != s and socket_in_list != sock:
                            try:
                                socket_in_list.send(data)
                            except Exception as e:
                                print(e.message)
                                socket_in_list.close()
                                c_list.remove(socket_in_list)
                                continue
                else:
                    print('Connection Closed {0}'.format(fd.index(sock)+3))
                    c_list.remove(sock)
                    sock.close()

    except KeyboardInterrupt:
        s.close()
        sys.exit()