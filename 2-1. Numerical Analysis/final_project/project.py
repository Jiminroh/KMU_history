import matplotlib.pyplot as plt
import numpy as np

plt.rc('font', family='Malgun Gothic')
plt.rc('axes', unicode_minus=False)

def f(x, y):
    return x**2/15 + y**2

def fg(x, y):
    return 2*x / 15, 2 * y

class SGD:
    def __init__(self, mu=0.01):
        self.mu = mu

    def update(self, v, g):
        for key in v.keys():
            v[key] -= self.mu * g[key]


if __name__ == '__main__':
    sgd = SGD(mu=0.38)
    v = {'x': 1., 'y': 1.}
    g = {'x': 0., 'y': 0.}
    xx = []
    yy = []
    for i in range(1):
        xx.append(v['x'])
        yy.append(v['y'])
        g['x'], g['y'] = fg(v['x'], v['y'])
        sgd.update(v, g)

    for x, y in zip(xx ,yy):
        print(x, y)

    x = np.linspace(-10, 10, 1000)
    y = np.linspace(-5, 5, 500)
    X, Y = np.meshgrid(x, y)
    Z = f(X, Y)

    mask = Z > 7
    Z[mask] = 0

    plt.contour(X, Y, Z, 10)
    plt.title('SDG')
    plt.xlabel('x')
    plt.ylabel('y')
    plt.axis('equal')
    plt.plot(0,0,'bo')
    plt.xlim(-7, 7)
    plt.ylim(-5, 5)
    plt.xticks(np.linspace(-7, 7, 5))
    plt.yticks(np.linspace(-5, 5, 5))
    plt.plot(xx, yy, '-o', color='red')
    plt.show()


#----------------------------------------------------------------------------------------------------------------------
def f2(x, y):
    return (1 - x)**2 + 100.0 * (y - x**2)**2

def f2g(x, y):
    return np.array((2.0 * (x - 1) - 400.0 * x * (y - x**2), 200.0 * (y - x**2)))


xx = np.linspace(-4, 4, 800)
yy = np.linspace(-3, 3, 600)
X, Y = np.meshgrid(xx, yy)
Z = f2(X, Y)

levels = np.logspace(-1, 3, 10)

plt.contourf(X, Y, Z, alpha=0.2, levels=levels)
plt.contour(X, Y, Z, colors="green", levels=levels, zorder=0)
plt.plot(1, 1, 'ro', markersize=10)

mu = 8e-4
s = 0.95
x, y = -1, -1

for i in range(5):
    g = f2g(x, y)
    plt.arrow(x, y, -s * mu * g[0], -s * mu * g[1],
              head_width=0.04, head_length=0.04, fc='k', ec='k', lw=2)
    x = x - mu * g[0]
    y = y - mu * g[1]

plt.xlim(-3, 3)
plt.ylim(-2, 2)
plt.xticks(np.linspace(-3, 3, 7))
plt.yticks(np.linspace(-2, 2, 5))
plt.xlabel("x")
plt.ylabel("y")
plt.title("로젠브록함수를 이용한 검증")
plt.show()
