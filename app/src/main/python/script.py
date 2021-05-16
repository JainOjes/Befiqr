import requests

def main():
    res = requests.get('https://ipinfo.io/')
    data = res.json()

    city = data['city']
    country = data['country']

    location = data['loc'].split(',')
    latitude = location[0]
    latitude = str(float(latitude))
    longitude = location[1]
    longitude = str(float(longitude))

    return "Help me! I am in danger as I am being harassed. My location is:" +"\nCountry name: " + country + "\nLatitude: " + latitude + "\nLongitude: " + longitude + "\nCity: " + city

main()