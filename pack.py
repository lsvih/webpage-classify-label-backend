# This script use to pack images and its corresponding clazz into a pickle pack
# For reduce the occupy of disk, this script would resize and tailor image to 200 * 200 px

# Name of output file is "dataset"

# Install dependency:
# pip3 install opencv-python
# pip3 install opencv-contrib-python
# pip3 install pymysql

import os
import re
import pickle
import cv2
import numpy as np
import pymysql

IMAGEDIR = "./uploads/image/"
WIDTH = 200


class DB():
    def __init__(self):
        with open("./src/main/resources/application.properties", "r") as f:
            java_config = "".join(f.readlines())
        server = re.search("mysql://(.*):3306/", java_config).group(1)
        username = re.search("username = (.*)\n", java_config).group(1)
        password = re.search("password = (.*)\n", java_config).group(1)
        db_name = re.search(server + ":3306" +
                            "/(.*)\?autoReconnect", java_config).group(1)
        self.db = pymysql.connect(server, username, password, db_name)
        self.cursor = self.db.cursor()

    def findClazz(self, uid):
        sql = "SELECT clazz FROM webpage_label WHERE id = '%d'" % (uid)
        try:
            self.cursor.execute(sql)
            rs = self.cursor.fetchall()[0]
            return rs[0]
        except Exception as e:
            print(e)
            return False

    def close(self):
        self.db.close()


def load_data():
    images = filter(lambda x: x[-4:] == ".png", os.listdir(IMAGEDIR))
    dataset = {"images": [], "clazz": []}
    query = DB()
    for image_name in images:
        image_id = int(image_name[:-4])
        image = cv2.imread(IMAGEDIR + image_name)
        [h, w, _] = image.shape
        scale = WIDTH / w
        image = cv2.resize(image, (WIDTH, int(h * scale)),
                           interpolation=cv2.INTER_AREA)[:WIDTH]
        # once image's height shorter than width, fill it with white
        if image.shape[0] < WIDTH:
            image = np.concatenate(image, np.tile(np.array(
                [[255, 255, 255]] * WIDTH), (WIDTH - image.shape[0], 1, 1))).astype("uint8")
        dataset["images"].append(image)
        clazz = query.findClazz(image_id)
        if clazz is not False:
            dataset["clazz"].append(clazz)
        else:
            query.close()
            raise Exception("数据库查询失败，id=%d" % (image_id))
    query.close()
    return dataset


def pack_data():
    with open("dataset", "wb") as f:
        pickle.dump(load_data(), f)


if __name__ == "__main__":
    print("packing data ...")
    pack_data()
