from twython import Twython
import sys

from auth import (
    consumer_key,
    consumer_secret,
    access_token,
    access_token_secret
)
twitter = Twython(
    consumer_key,
    consumer_secret,
    access_token,
    access_token_secret
)
def post(message,path):
    image = open(path,'rb')
    response = twitter.upload_media(media=image)
    media_id = [response['media_id']]
    a=twitter.update_status(status=message,media_ids=media_id)
    id = a["id"]
    return id
def postReply(message,path,oldId):
    image = open(path,'rb')
    response = twitter.upload_media(media=image)
    media_id = [response['media_id']]
    a=twitter.update_status(status=message,media_ids=media_id,in_reply_to_status_id=oldId)
    id = a["id"]
    return id
id = post("Updated deployment graph from the Starlink V0.9 Mission","graphs/l0.png")
id = postReply("Updated deployment graph from the Starlink V1.0-L1 Mission","graphs/l1.png",id)
id = postReply("Updated deployment graph from the Starlink V1.0-L2 Mission","graphs/l2.png",id)
id = postReply("Updated deployment graph from the Starlink V1.0-L3 Mission","graphs/l3.png",id)
twitter.update_status(status="All date based on Supplemental TLEs from Celestrak.com",in_reply_to_status_id=id)
